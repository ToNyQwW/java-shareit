package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
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
import java.util.List;

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
    @Transactional(readOnly = true)
    public BookingDto getBooking(long userId, long bookingId) {
        Booking booking = getBookingOrElseThrow(bookingId);

        throwIfUserNotItemOwnerOrBooker(userId, booking);

        log.info("Get booking: {}", booking);
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getUserBookings(long userId, BookingState state) {
        getUserOrElseThrow(userId);

        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        LocalDateTime now = LocalDateTime.now();

        List<Booking> result = switch (state) {
            case ALL -> bookingRepository.findAllByBookerId(userId, sort);
            case PAST -> bookingRepository.findAllByBookerIdAndEndBefore(userId, now, sort);
            case FUTURE -> bookingRepository.findAllByBookerIdAndStartAfter(userId, now, sort);
            case WAITING -> bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.WAITING, sort);
            case REJECTED -> bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.REJECTED, sort);
            case CURRENT -> bookingRepository.findCurrentByBookerId(userId, now, sort);
        };

        log.info("getUserBookings result: {}", result);
        return result.stream()
                .map(bookingMapper::toBookingDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getOwnerBookings(long userId, BookingState state) {
        getUserOrElseThrow(userId);

        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        LocalDateTime now = LocalDateTime.now();

        List<Booking> result = switch (state) {
            case ALL -> bookingRepository.findAllByItemOwnerId(userId, sort);
            case PAST -> bookingRepository.findAllByItemOwnerIdAndEndBefore(userId, now, sort);
            case FUTURE -> bookingRepository.findAllByItemOwnerIdAndStartAfter(userId, now, sort);
            case WAITING -> bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.WAITING, sort);
            case REJECTED -> bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED, sort);
            case CURRENT -> bookingRepository.findCurrentByOwnerId(userId, now, sort);
        };

        log.info("getOwnerBookings result: {}", result);
        return result.stream()
                .map(bookingMapper::toBookingDto)
                .toList();
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