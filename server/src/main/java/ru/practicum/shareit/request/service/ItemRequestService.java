package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createItemRequest(long requestorId, ItemRequestCreateDto itemRequestCreateDto);

    ItemRequestWithItemsDto getItemRequest(long requestId);

    List<ItemRequestDto>  getAllItemRequests(long requestorId);

    List<ItemRequestWithItemsDto>  getUserItemRequests(long requestorId);
}