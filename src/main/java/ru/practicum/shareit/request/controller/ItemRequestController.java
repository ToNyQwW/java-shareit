package ru.practicum.shareit.request.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static ru.practicum.shareit.util.RequestHeaderConstants.USER_ID_HEADER;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto createItemRequest(@RequestHeader(USER_ID_HEADER) long requestorId,
                                            @RequestBody @Valid ItemRequestCreateDto itemRequestCreateDto) {
        return itemRequestService.createItemRequest(requestorId, itemRequestCreateDto);
    }

    @GetMapping
    public List<ItemRequestWithItemsDto> getUserItemRequests(@RequestHeader(USER_ID_HEADER) long requestorId) {
        return itemRequestService.getUserItemRequests(requestorId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests(@RequestHeader(USER_ID_HEADER) long requestorId) {
        return itemRequestService.getAllItemRequests(requestorId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithItemsDto getItemRequest(@PathVariable long requestId) {
        return itemRequestService.getItemRequest(requestId);
    }
}