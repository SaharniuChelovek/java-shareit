package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    // GET /requests — свои запросы, от новых к старым
    List<ItemRequest> findAllByRequestorIdOrderByCreatedDesc(Long requestorId);

    // GET /requests/all — чужие запросы, от новых к старым
    List<ItemRequest> findAllByRequestorIdNotOrderByCreatedDesc(Long requestorId);
}
