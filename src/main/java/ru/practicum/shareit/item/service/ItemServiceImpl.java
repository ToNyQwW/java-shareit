package ru.practicum.shareit.item.service;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static java.util.Collections.emptyList;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    public ItemDto createItem(long userId, ItemCreateDto itemCreateDto) {
        User user = getUserOrElseThrow(userId);

        Item item = itemMapper.toItem(itemCreateDto);
        item.setOwner(user);
        Item createdItem = itemRepository.save(item);
        log.info("Item created: {}", createdItem);

        return itemMapper.toItemDto(createdItem);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getItem(long id) {
        Item item = getItemOrElseThrow(id);
        log.info("get Item: {}", item);

        return itemMapper.toItemDto(item);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> searchItems(String search) {
        if (search == null || search.isBlank()) {
            return emptyList();
        }

        List<ItemDto> result = itemRepository.searchItems(search.toLowerCase())
                .stream()
                .map(itemMapper::toItemDto)
                .toList();
        log.info("searchItems result: {}", result);

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemWithBookingsDto> getUserItems(long userId) {
        getUserOrElseThrow(userId);

        List<Item> items = itemRepository.findAllByOwnerId(userId);

        if (items.isEmpty()) {
            return emptyList();
        }

        List<Booking> bookings = bookingRepository.findAllByItemOwnerId(userId);

        List<ItemWithBookingsDto> result = items.stream()
                .map(item -> toItemWithBookingsDto(item, bookings))
                .toList();
        log.info("getUserItems result: {}", result);
        return result;
    }

    private ItemWithBookingsDto toItemWithBookingsDto(Item item, List<Booking> bookings) {
        LocalDateTime now = LocalDateTime.now();
        ItemWithBookingsDto result = itemMapper.toItemWithBookingsDto(item);

        List<Booking> itemBookings = bookings.stream()
                .filter(booking -> booking.getItem().getId() == item.getId())
                .toList();

        Booking lastBooking = itemBookings.stream()
                .filter(booking -> booking.getEnd().isBefore(now))
                .max(Comparator.comparing(Booking::getEnd))
                .orElse(null);

        Booking nextBooking = itemBookings.stream()
                .filter(booking -> booking.getStart().isAfter(now))
                .min(Comparator.comparing(Booking::getStart))
                .orElse(null);

        result.setLastBooking(lastBooking != null ? bookingMapper.toBookingDto(lastBooking) : null);
        result.setNextBooking(nextBooking != null ? bookingMapper.toBookingDto(nextBooking) : null);

        return result;
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemUpdateDto itemUpdateDto) {
        getUserOrElseThrow(userId);
        Item item = getItemOrElseThrow(itemId);
        throwIfUserNotItemOwner(userId, item);

        updateItemFields(item, itemUpdateDto);
        itemRepository.save(item);
        log.info("Item updated: {}", item);

        return itemMapper.toItemDto(item);
    }

    private void updateItemFields(Item item, ItemUpdateDto itemUpdateDto) {
        String name = itemUpdateDto.getName();
        if (name != null && !name.isBlank()) {
            item.setName(name);
        }

        String description = itemUpdateDto.getDescription();
        if (description != null && !description.isBlank()) {
            item.setDescription(description);
        }

        Boolean available = itemUpdateDto.getAvailable();
        if (available != null) {
            item.setAvailable(available);
        }
    }

    private User getUserOrElseThrow(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
    }

    private Item getItemOrElseThrow(long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item with id " + id + " not found"));
    }

    private void throwIfUserNotItemOwner(long userId, Item item) {
        if (item.getOwner().getId() != userId) {
            throw new AccessDeniedException("User " + userId + " is not the owner of item " + item.getId());
        }
    }
}