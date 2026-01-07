package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookingServiceTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @BeforeAll
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        User owner = userRepository.save(
                User.builder()
                        .name("owner")
                        .email("owner@test.com")
                        .build()
        );

        User booker = userRepository.save(
                User.builder()
                        .name("booker")
                        .email("booker@test.com")
                        .build()
        );

        userRepository.save(
                User.builder()
                        .name("testUser")
                        .email("testUser@test.com")
                        .build()
        );

        Item item = itemRepository.save(
                Item.builder()
                        .name("item")
                        .description("itemDescription")
                        .available(true)
                        .owner(owner)
                        .build()
        );

        bookingRepository.save(
                Booking.builder()
                        .start(now.minusDays(3))
                        .end(now.minusDays(1))
                        .booker(booker)
                        .item(item)
                        .status(BookingStatus.APPROVED)
                        .build()
        );

        bookingRepository.save(
                Booking.builder()
                        .start(now.plusDays(2))
                        .end(now.plusDays(3))
                        .booker(booker)
                        .item(item)
                        .status(BookingStatus.APPROVED)
                        .build()
        );
    }

    @Test
    void createBooking_whenUserNotFound_thenNotFoundExceptionThrown() {
        long userId = 10000L;
        Item item = itemRepository.findById(1L).orElseThrow();
        LocalDateTime now = LocalDateTime.now();
        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .start(now)
                .end(now.plusDays(1))
                .itemId(item.getId())
                .build();

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(userId, bookingCreateDto));
        assertEquals("User with id " + userId + " not found", notFoundException.getMessage());
    }

    @Test
    void createBooking_whenItemNotFound_thenNotFoundExceptionThrown() {
        long itemId = 10000L;
        User user = userRepository.findById(1L).orElseThrow();
        LocalDateTime now = LocalDateTime.now();
        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .start(now)
                .end(now.plusDays(1))
                .itemId(itemId)
                .build();

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(user.getId(), bookingCreateDto));
        assertEquals("Item with id " + itemId + " not found", notFoundException.getMessage());
    }

    @Test
    void createBooking_whenItemNotAvailable_thenItemNotAvailableExceptionThrown() {
        User user = userRepository.findById(1L).orElseThrow();
        Item item = itemRepository.findById(1L).orElseThrow();
        item.setAvailable(false);
        itemRepository.save(item);
        LocalDateTime now = LocalDateTime.now();
        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .start(now)
                .end(now.plusDays(1))
                .itemId(item.getId())
                .build();

        ItemNotAvailableException itemNotAvailableException = assertThrows(ItemNotAvailableException.class,
                () -> bookingService.createBooking(user.getId(), bookingCreateDto));
        assertEquals("Item is not available", itemNotAvailableException.getMessage());

    }

    @Test
    void createBooking_whenUserItemOwner_thenItemOwnerBookingExceptionThrown() {
        User owner = userRepository.findById(1L).orElseThrow();
        Item item = itemRepository.findById(1L).orElseThrow();
        LocalDateTime now = LocalDateTime.now();

        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .start(now)
                .end(now.plusDays(1))
                .itemId(item.getId())
                .build();

        ItemOwnerBookingException itemOwnerBookingException = assertThrows(ItemOwnerBookingException.class,
                () -> bookingService.createBooking(owner.getId(), bookingCreateDto));
        assertEquals("User " + owner.getId() + " cannot book their own item " +
                item.getId(), itemOwnerBookingException.getMessage());
    }

    @Test
    void getBooking_whenBookingNotFound_thenNotFoundExceptionThrown() {
        long bookingId = 10000L;
        User user = userRepository.findById(1L).orElseThrow();

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.getBooking(user.getId(), bookingId));
        assertEquals("Booking with id " + bookingId + " not found", notFoundException.getMessage());
    }

    @Test
    void getBooking_whenUserNotItemOwnerOrBooker_thenAccessDeniedExceptionThrown() {
        User user = userRepository.findById(3L).orElseThrow();
        Booking booking = bookingRepository.findById(1L).orElseThrow();

        AccessDeniedException accessDeniedException = assertThrows(AccessDeniedException.class,
                () -> bookingService.getBooking(user.getId(), booking.getId()));
        assertEquals("User " + user.getId() + " is not the owner or Booker of item " +
                booking.getId(), accessDeniedException.getMessage());
    }

    @Test
    void getUserBookings_whenUserNotFound_thenNotFoundExceptionThrown() {
        long userId = 10000L;

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.getUserBookings(userId, BookingState.ALL));
        assertEquals("User with id " + userId + " not found", notFoundException.getMessage());
    }

    @Test
    void getUserBookings_whenBookingStateAll_shouldReturnAllBookings() {
        User booker = userRepository.findById(2L).orElseThrow();

        List<BookingDto> bookings = bookingService.getUserBookings(booker.getId(), BookingState.ALL);

        assertEquals(2, bookings.size());
    }

    @Test
    void getUserBookings_whenBookingStatePast_shouldReturnPastBookings() {
        User booker = userRepository.findById(2L).orElseThrow();
        LocalDateTime now = LocalDateTime.now();

        List<BookingDto> bookings = bookingService.getUserBookings(booker.getId(), BookingState.PAST);

        assertEquals(1, bookings.size());
        assertTrue(bookings.getFirst().getEnd().isBefore(now));
    }

    @Test
    void getUserBookings_whenBookingStateFuture_shouldReturnFutureBookings() {
        User booker = userRepository.findById(2L).orElseThrow();
        LocalDateTime now = LocalDateTime.now();

        List<BookingDto> bookings = bookingService.getUserBookings(booker.getId(), BookingState.FUTURE);

        assertEquals(1, bookings.size());
        assertTrue(bookings.getFirst().getStart().isAfter(now));
    }

    @Test
    void getUserBookings_whenBookingStateWaiting_shouldReturnWaitingBookings() {
        User booker = userRepository.findById(2L).orElseThrow();
        bookingRepository.findAll()
                .forEach(booking -> {
                    booking.setStatus(BookingStatus.WAITING);
                    bookingRepository.save(booking);
                });

        List<BookingDto> bookings = bookingService.getUserBookings(booker.getId(), BookingState.WAITING);

        assertEquals(2, bookings.size());
        bookings.forEach(bookingDto -> assertEquals(BookingStatus.WAITING, bookingDto.getStatus()));
    }

    @Test
    void getUserBookings_whenBookingStateRejected_shouldReturnRejectedBookings() {
        User booker = userRepository.findById(2L).orElseThrow();
        bookingRepository.findAll()
                .forEach(booking -> {
                    booking.setStatus(BookingStatus.REJECTED);
                    bookingRepository.save(booking);
                });

        List<BookingDto> bookings = bookingService.getUserBookings(booker.getId(), BookingState.REJECTED);

        assertEquals(2, bookings.size());
        bookings.forEach(bookingDto -> assertEquals(BookingStatus.REJECTED, bookingDto.getStatus()));
    }

    @Test
    void getUserBookings_whenBookingStateCurrent_shouldReturnEmptyList() {
        User booker = userRepository.findById(2L).orElseThrow();

        List<BookingDto> bookings = bookingService.getUserBookings(booker.getId(), BookingState.CURRENT);

        assertEquals(0, bookings.size());
    }

    @Test
    void getOwnerBooking_whenOwnerItemsNotBooking_shouldReturnEmptyList() {
        User booker = userRepository.findById(2L).orElseThrow();

        List<BookingDto> bookings = bookingService.getOwnerBookings(booker.getId(), BookingState.ALL);

        assertEquals(0, bookings.size());
    }

    @Test
    void getOwnerBookings_whenUserNotFound_thenNotFoundExceptionThrown() {
        long userId = 10000L;

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.getOwnerBookings(userId, BookingState.ALL));
        assertEquals("User with id " + userId + " not found", notFoundException.getMessage());
    }

    @Test
    void getOwnerBookings_whenBookingStateAll_shouldReturnAllBookings() {
        User owner = userRepository.findById(1L).orElseThrow();

        List<BookingDto> bookings = bookingService.getOwnerBookings(owner.getId(), BookingState.ALL);

        assertEquals(2, bookings.size());
    }

    @Test
    void getOwnerBookings_whenBookingStatePast_shouldReturnPastBookings() {
        User owner = userRepository.findById(1L).orElseThrow();

        List<BookingDto> bookings = bookingService.getOwnerBookings(owner.getId(), BookingState.PAST);
        assertEquals(1, bookings.size());
    }

    @Test
    void getOwnerBookings_whenBookingStateFuture_shouldReturnFutureBookings() {
        User owner = userRepository.findById(1L).orElseThrow();

        List<BookingDto> bookings = bookingService.getOwnerBookings(owner.getId(), BookingState.FUTURE);
        assertEquals(1, bookings.size());
    }

    @Test
    void getOwnerBookings_whenBookingStateWaiting_shouldReturnWaitingBookings() {
        User owner = userRepository.findById(1L).orElseThrow();
        bookingRepository.findAll()
                .forEach(booking -> {
                    booking.setStatus(BookingStatus.WAITING);
                    bookingRepository.save(booking);
                });

        List<BookingDto> bookings = bookingService.getOwnerBookings(owner.getId(), BookingState.WAITING);

        assertEquals(2, bookings.size());
        bookings.forEach(bookingDto -> assertEquals(BookingStatus.WAITING, bookingDto.getStatus()));
    }

    @Test
    void getOwnerBookings_whenBookingStateRejected_shouldReturnRejectedBookings() {
        User owner = userRepository.findById(1L).orElseThrow();
        bookingRepository.findAll()
                .forEach(booking -> {
                    booking.setStatus(BookingStatus.REJECTED);
                    bookingRepository.save(booking);
                });

        List<BookingDto> bookings = bookingService.getOwnerBookings(owner.getId(), BookingState.REJECTED);

        assertEquals(2, bookings.size());
        bookings.forEach(bookingDto -> assertEquals(BookingStatus.REJECTED, bookingDto.getStatus()));
    }

    @Test
    void getOwnerBookings_whenBookingStateCurrent_shouldReturnEmptyList() {
        User owner = userRepository.findById(1L).orElseThrow();

        List<BookingDto> bookings = bookingService.getOwnerBookings(owner.getId(), BookingState.CURRENT);

        assertEquals(0, bookings.size());
    }

    @Test
    void approveBooking_whenBookingNotFound_thenNotFoundExceptionThrown() {
        long bookingId = 10000L;
        User user = userRepository.findById(1L).orElseThrow();

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.approveBooking(user.getId(), bookingId, true));
        assertEquals("Booking with id " + bookingId + " not found", notFoundException.getMessage());
    }

    @Test
    void approveBooking_whenUserNotItemOwner_thenAccessDeniedExceptionThrown() {
        User user = userRepository.findById(2L).orElseThrow();
        Booking booking = bookingRepository.findById(1L).orElseThrow();

        AccessDeniedException accessDeniedException = assertThrows(AccessDeniedException.class,
                () -> bookingService.approveBooking(user.getId(), booking.getId(), true));
        assertEquals("User " + user.getId() + " is not the owner of item " +
                booking.getItem().getId(), accessDeniedException.getMessage());
    }

    @Test
    void approveBooking_whenOwnerApprovedBooking_shouldChangedStatus() {
        User owner = userRepository.findById(1L).orElseThrow();
        Booking booking = bookingRepository.findById(1L).orElseThrow();
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);

        BookingDto bookingDto = bookingService.approveBooking(owner.getId(), booking.getId(), true);
        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());
    }

    @Test
    void approveBooking_whenOwnerRejectedBooking_shouldChangedStatus() {
        User owner = userRepository.findById(1L).orElseThrow();
        Booking booking = bookingRepository.findById(1L).orElseThrow();

        BookingDto bookingDto = bookingService.approveBooking(owner.getId(), booking.getId(), false);
        assertEquals(BookingStatus.REJECTED, bookingDto.getStatus());
    }
}