package ru.practicum.shareit.request.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import static ru.practicum.shareit.util.RequestHeaderConstants.USER_ID_HEADER;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader(USER_ID_HEADER) long requestorId,
                                            @RequestBody @Valid ItemRequestCreateDto itemRequestCreateDto) {
        return itemRequestClient.createItemRequest(requestorId, itemRequestCreateDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItemRequests(@RequestHeader(USER_ID_HEADER) long requestorId) {
        return itemRequestClient.getUserItemRequests(requestorId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(@RequestHeader(USER_ID_HEADER) long requestorId) {
        return itemRequestClient.getAllItemRequests(requestorId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@PathVariable long requestId) {
        return itemRequestClient.getItemRequest(requestId);
    }
}