package ru.practicum.shareit.request.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;

import java.util.List;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {


    @Override
    public ItemRequestDto createItemRequest(long requestorId, ItemRequestCreateDto itemRequestCreateDto) {
        return null;
    }

    @Override
    public ItemRequestWithItemsDto getItemRequest(long requestId) {
        return null;
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(long requestorId) {
        return List.of();
    }

    @Override
    public List<ItemRequestWithItemsDto> getUserItemRequests(long requestorId) {
        return List.of();
    }
}
