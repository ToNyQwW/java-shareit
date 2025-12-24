package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {

    ItemDto createItem(long userId, ItemCreateDto itemCreateDto);

    ItemWithAdditionalInfoDto getItem(long userId, long itemId);

    List<ItemDto> searchItems(String search);

    List<ItemWithAdditionalInfoDto> getUserItems(long userId);

    ItemDto updateItem(long userId, long itemId, ItemUpdateDto itemUpdateDto);

    CommentDto createComment(long userId, long itemId, CommentCreateDto commentCreateDto);
}