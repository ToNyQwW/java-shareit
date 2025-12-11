package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static ru.practicum.shareit.util.RequestHeaderConstants.USER_ID_HEADER;

@RestController
@AllArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(@RequestHeader(USER_ID_HEADER) long userId,
                              @RequestBody @Valid ItemCreateDto itemCreateDto) {
        return itemService.createItem(userId, itemCreateDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable long itemId) {
        return itemService.getItem(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam(name = "text") String search) {
        return itemService.searchItems(search);
    }

    @GetMapping
    public List<ItemDto> getUserItems(@RequestHeader(USER_ID_HEADER) long userId) {
        return itemService.getUserItems(userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(USER_ID_HEADER) long userId,
                              @PathVariable long itemId,
                              @RequestBody @Valid ItemUpdateDto itemUpdateDto) {
        return itemService.updateItem(userId, itemId, itemUpdateDto);
    }
}