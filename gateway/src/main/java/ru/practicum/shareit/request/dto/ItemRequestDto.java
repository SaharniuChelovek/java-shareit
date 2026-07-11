package ru.practicum.shareit.request.dto;



import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
public class ItemRequestDto {

    private Long id;

    private String description;

    //пока пусть будет string потом перепишем(тут был User)
    private String requestor;

    private LocalDateTime created;

}
