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
    public ItemDto addItem(ItemCreateDto itemCreateDto) {
        //TODO после реализации user доделать логику метода и всего сервиса
        Item item = itemMapper.toItem(itemCreateDto);
        return null;
    }

    @Override
    public ItemDto getItem(long id) {
        return null;
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        return List.of();
    }

    @Override
    public List<ItemDto> getUserItems(long userId) {
        return List.of();
    }

    @Override
    public void updateItem(ItemUpdateDto itemUpdateDto) {

    }
}