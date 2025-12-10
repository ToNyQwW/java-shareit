package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerId(long userId);

    @Query("""
            SELECT i FROM Item i
            WHERE i.available = true
            AND (LOWER(i.name) like :search
                 OR LOWER(i.description) like :search)"""
    )
    List<Item> searchItems(@Param("search") String search);
}