package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;

    @Override
    public ItemDto createItem(ItemCreateDto itemCreateDto) {
        return itemMapper.toItemDto(itemRepository.createItem(itemMapper.toItem(itemCreateDto)));
    }

    @Override
    public ItemDto getItem(long id) {
        return itemMapper.toItemDto(itemRepository.getItem(id).get());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        return itemRepository.searchItems(text.toLowerCase())
                .stream()
                .map(itemMapper::toItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> getUserItems(long userId) {
        return itemRepository.getUserItems(userId)
                .stream()
                .map(itemMapper::toItemDto)
                .toList();
    }

    @Override
    public void updateItem(long userId, long itemId, ItemUpdateDto itemUpdateDto) {

        Item item = itemRepository.getItem(itemId).get();
        item.setName(itemUpdateDto.getName());
        item.setDescription(itemUpdateDto.getDescription());
        item.setAvailable(itemUpdateDto.isAvailable());

        itemRepository.updateItem(item);
    }
}