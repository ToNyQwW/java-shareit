package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(ItemCreateDto itemCreateDto);

    ItemDto getItem(long id);

    List<ItemDto> searchItems(String text);

    List<ItemDto> getUserItems(long userId);

    void updateItem(ItemUpdateDto itemUpdateDto);
}