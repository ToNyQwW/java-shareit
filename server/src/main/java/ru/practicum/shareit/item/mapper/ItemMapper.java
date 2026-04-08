package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithAdditionalInfoDto;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(source = "owner.id", target = "ownerId")
    ItemDto toItemDto(Item item);

    Item toItem(ItemCreateDto itemCreateDto);

    ItemWithAdditionalInfoDto toItemWithAdditionalInfoDto(Item item);
}