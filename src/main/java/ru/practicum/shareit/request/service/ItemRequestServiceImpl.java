package ru.practicum.shareit.request.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    @Override
    public ItemRequestDto createItemRequest(long requestorId, ItemRequestCreateDto itemRequestCreateDto) {
        return null;
    }
}
