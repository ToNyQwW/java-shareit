package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

public interface ItemRequestService {

    ItemRequestDto createItemRequest(long requestorId, ItemRequestCreateDto itemRequestCreateDto);
}
