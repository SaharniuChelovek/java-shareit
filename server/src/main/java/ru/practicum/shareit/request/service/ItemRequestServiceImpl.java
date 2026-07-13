package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemDbRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemAnswerDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserDbRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserDbRepository userRepository;
    private final ItemDbRepository itemRepository;



    @Override
    public ItemRequestDto createRequest(Long userId, CreateItemRequestDto createItemRequestDto) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        ItemRequest request = ItemRequestMapper.toItemRequest(createItemRequestDto);
        request.setRequestor(requestor);

        ItemRequest savedRequest = itemRequestRepository.save(request);
        return ItemRequestMapper.toItemRequestDto(savedRequest, new ArrayList<>());
    }

    @Override
    public List<ItemRequestDto> getOwnRequests(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }

        List<ItemRequest> requests = itemRequestRepository
                .findAllByRequestorIdOrderByCreatedDesc(userId);

        return buildItemRequestDtos(requests);
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }

        List<ItemRequest> requests = itemRequestRepository
                .findAllByRequestorIdNotOrderByCreatedDesc(userId);

        return buildItemRequestDtos(requests);
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }

        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден"));

        List<ItemAnswerDto> items = itemRepository.findAllByRequestIdIn(List.of(requestId))
                .stream()
                .map(item -> new ItemAnswerDto(item.getId(), item.getName(), item.getOwner().getId()))
                .toList();

        return ItemRequestMapper.toItemRequestDto(request, items);
    }

    private List<ItemRequestDto> buildItemRequestDtos(List<ItemRequest> requests) {
        List<Long> requestIds = requests.stream().map(ItemRequest::getId).toList();

        List<Item> items = itemRepository.findAllByRequestIdIn(requestIds);

        return requests.stream().map(request -> {
            List<ItemAnswerDto> itemAnswers = items.stream()
                    .filter(item -> item.getRequest() != null &&
                            item.getRequest().getId().equals(request.getId()))
                    .map(item -> new ItemAnswerDto(
                            item.getId(),
                            item.getName(),
                            item.getOwner().getId()))
                    .toList();
            return ItemRequestMapper.toItemRequestDto(request, itemAnswers);
        }).toList();
    }
}
