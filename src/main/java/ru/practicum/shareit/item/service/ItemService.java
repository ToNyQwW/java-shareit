package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

public interface ItemService {

    ItemDto createItem(long userId, ItemCreateDto itemCreateDto);

    ItemDto getItem(long id);

    List<ItemDto> searchItems(String search);

    List<ItemDto> getUserItems(long userId);

    ItemDto updateItem(long userId, long itemId, ItemUpdateDto itemUpdateDto);
}