package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Item createItem(Item item);

    Optional<Item> getItem(long id);

    List<Item> searchItems(String text);

    List<Item> getUserItems(long userId);

    void updateItem(Item item);
}