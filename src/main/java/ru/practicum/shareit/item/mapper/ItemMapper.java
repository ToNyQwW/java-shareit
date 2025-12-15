package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithAdditionalInfoDto;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemMapper {

    public ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name((item.getName()))
                .description(item.getDescription())
                .available(item.isAvailable())
                .build();
    }

    public Item toItem(ItemCreateDto itemCreateDto) {
        return Item.builder()
                .name(itemCreateDto.getName())
                .description(itemCreateDto.getDescription())
                .available(itemCreateDto.getAvailable())
                .build();
    }

    public ItemWithAdditionalInfoDto toItemWithAdditionalInfoDto(Item item) {
        return ItemWithAdditionalInfoDto.builder()
                .id(item.getId())
                .name((item.getName()))
                .description(item.getDescription())
                .available(item.isAvailable())
                .build();
    }
}