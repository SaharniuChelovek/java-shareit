package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {

    public static Item toItem(CreateItemDto dto) {
        Item item = new Item();
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());

        item.setAvailable(dto.getAvailable() != null ? dto.getAvailable() : false);

        return item;
    }

    public static void updateItemFromDto(UpdateItemDto dto, Item oldItem) {
        if (dto.getName() != null) {
            oldItem.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            oldItem.setDescription(dto.getDescription());
        }
        if (dto.getAvailable() != null) {
            oldItem.setAvailable(dto.getAvailable());
        }
    }

    public static ItemDto toItemDto(Item item) {

        Long ownerId = item.getOwner() != null ? item.getOwner().getId() : null;


        Long requestId = item.getRequest() != null ? item.getRequest().getId() : null;

        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                ownerId,
                requestId
        );
    }
}
