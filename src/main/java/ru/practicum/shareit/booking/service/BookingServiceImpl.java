package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.exception.ItemOwnerBookingException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingMapper bookingMapper;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    public BookingDto createBooking(long userId, BookingCreateDto bookingCreateDto) {
        User user = getUserOrElseThrow(userId);
        Item item = getItemOrElseThrow(bookingCreateDto.getItemId());

        throwIfItemNotAvailable(item);
        throwIfUserItemOwner(userId, item);

        Booking booking = bookingMapper.toBooking(bookingCreateDto);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        Booking createdBooking = bookingRepository.save(booking);
        log.info("Booking created: {}", createdBooking);
        return bookingMapper.toBookingDto(createdBooking);
    }

    @Override
    public BookingDto getBooking(long userId, long bookingId) {
        Booking booking = getBookingOrElseThrow(bookingId);

        throwIfUserNotItemOwnerOrBooker(userId, booking);

        log.info("Get booking: {}", booking);
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getUserBookings(long userId, BookingState state) {
        User user = getUserOrElseThrow(userId);

        List<BookingDto> result = user.getBookings().stream()
                .filter(getBookingPredicate(state))
                .sorted(Comparator.comparing(Booking::getStart))
                .map(bookingMapper::toBookingDto)
                .toList();
        log.info("getUserBookings result: {}", result);
        return result;
    }

    @Override
    public List<BookingDto> getOwnerBookings(long userId, BookingState state) {
        getUserOrElseThrow(userId);

        List<BookingDto> result = bookingRepository.findAllByItem_Owner_Id(userId).stream()
                .filter(getBookingPredicate(state))
                .sorted(Comparator.comparing(Booking::getStart))
                .map(bookingMapper::toBookingDto)
                .toList();
        log.info("getOwnerBookings result: {}", result);
        return result;
    }

    @Override
    public BookingDto approveBooking(long userId, long bookingId, boolean approved) {
        Booking booking = getBookingOrElseThrow(bookingId);
        Item item = booking.getItem();

        throwIfUserNotItemOwner(userId, item);

        BookingStatus oldStatus = booking.getStatus();
        BookingStatus newStatus = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(newStatus);

        bookingRepository.save(booking);
        log.info("Booking status changed for booking {}: from {} to {}", bookingId, oldStatus, newStatus);
        return bookingMapper.toBookingDto(booking);
    }

    private Predicate<Booking> getBookingPredicate(BookingState state) {
        LocalDateTime now = LocalDateTime.now();
        return switch (state) {
            case ALL -> booking -> true;
            case PAST -> booking -> booking.getEnd().isBefore(now);
            case FUTURE -> booking -> booking.getStart().isAfter(now);
            case WAITING -> booking -> booking.getStatus() == BookingStatus.WAITING;
            case REJECTED -> booking -> booking.getStatus() == BookingStatus.REJECTED;
            case CURRENT -> booking -> !booking.getStart().isAfter(now) && !booking.getEnd().isBefore(now);
        };
    }

    private User getUserOrElseThrow(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
    }

    private Item getItemOrElseThrow(long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item with id " + id + " not found"));
    }

    private Booking getBookingOrElseThrow(long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking with id " + id + " not found"));
    }

    private void throwIfItemNotAvailable(Item item) {
        if (!item.isAvailable()) {
            throw new ItemNotAvailableException("Item is not available");
        }
    }

    private void throwIfUserItemOwner(long userId, Item item) {
        if (item.getOwner().getId() == userId) {
            throw new ItemOwnerBookingException("User " + userId + " cannot book their own item " + item.getId());
        }
    }

    private void throwIfUserNotItemOwner(long userId, Item item) {
        if (item.getOwner().getId() != userId) {
            throw new AccessDeniedException("User " + userId + " is not the owner of item " + item.getId());
        }
    }

    private void throwIfUserNotItemOwnerOrBooker(long userId, Booking booking) {
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new AccessDeniedException("User " + userId + " is not the owner or Booker of item " + booking.getId());
        }
    }
}