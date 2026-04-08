package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static ru.practicum.shareit.util.RequestHeaderConstants.USER_ID_HEADER;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(@RequestHeader(USER_ID_HEADER) long userId,
                              @RequestBody ItemCreateDto itemCreateDto) {
        return itemService.createItem(userId, itemCreateDto);
    }

    @GetMapping("/{itemId}")
    public ItemWithAdditionalInfoDto getItem(@RequestHeader(USER_ID_HEADER) long userId,
                                             @PathVariable long itemId) {
        return itemService.getItem(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam(name = "text") String search) {
        return itemService.searchItems(search);
    }

    @GetMapping
    public List<ItemWithAdditionalInfoDto> getUserItems(@RequestHeader(USER_ID_HEADER) long userId) {
        return itemService.getUserItems(userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(USER_ID_HEADER) long userId,
                              @PathVariable long itemId,
                              @RequestBody ItemUpdateDto itemUpdateDto) {
        return itemService.updateItem(userId, itemId, itemUpdateDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(USER_ID_HEADER) long userId,
                                 @PathVariable long itemId,
                                 @RequestBody CommentCreateDto commentCreateDto) {
        return itemService.createComment(userId, itemId, commentCreateDto);
    }
}