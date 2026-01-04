package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class BookingServiceTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private Item item;
    private User owner;
    private User booker;
    private User testUser;
    private Booking booking;
    private Booking testBooking;
    private BookingCreateDto bookingCreateDto;

    private LocalDateTime nowTime;

    @BeforeEach
    void setUp() {
        nowTime = LocalDateTime.now();

        owner = User.builder()
                .name("owner")
                .email("owner@test.com")
                .build();
        userRepository.save(owner);

        booker = User.builder()
                .name("booker")
                .email("booker@test.com")
                .build();
        userRepository.save(booker);

        testUser = User.builder()
                .name("testUser")
                .email("testUser@test.com")
                .build();
        userRepository.save(testUser);

        item = Item.builder()
                .name("item")
                .description("itemDescription")
                .available(true)
                .owner(owner)
                .build();
        itemRepository.save(item);

        booking = Booking.builder()
                .start(nowTime.minusHours(1))
                .end(nowTime.plusHours(1))
                .booker(booker)
                .item(item)
                .status(BookingStatus.APPROVED)
                .build();
        bookingRepository.save(booking);

        testBooking = Booking.builder()
                .start(nowTime.plusDays(2))
                .end(nowTime.plusDays(3))
                .booker(booker)
                .item(item)
                .status(BookingStatus.APPROVED)
                .build();
        bookingRepository.save(testBooking);

        bookingCreateDto = BookingCreateDto.builder()
                .start(nowTime)
                .end(nowTime.plusDays(1L))
                .itemId(item.getId())
                .build();

    }

    @Test
    void createBooking_whenUserNotFound_thenNotFoundExceptionThrown() {
        long userId = 10000L;

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(userId, bookingCreateDto));
        assertEquals("User with id " + userId + " not found", notFoundException.getMessage());
    }

    @Test
    void createBooking_whenItemNotFound_thenNotFoundExceptionThrown() {
        long userId = testUser.getId();
        long itemId = 10000L;

        bookingCreateDto.setItemId(itemId);

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(userId, bookingCreateDto));
        assertEquals("Item with id " + itemId + " not found", notFoundException.getMessage());
    }

    @Test
    void createBooking_whenItemNotAvailable_thenItemNotAvailableExceptionThrown() {
        long userId = testUser.getId();

        item.setAvailable(false);

        ItemNotAvailableException itemNotAvailableException = assertThrows(ItemNotAvailableException.class,
                () -> bookingService.createBooking(userId, bookingCreateDto));
        assertEquals("Item is not available", itemNotAvailableException.getMessage());
    }

    @Test
    void createBooking_whenUserItemOwner_thenItemOwnerBookingExceptionThrown() {
        long userId = owner.getId();

        ItemOwnerBookingException itemOwnerBookingException = assertThrows(ItemOwnerBookingException.class,
                () -> bookingService.createBooking(userId, bookingCreateDto));
        assertEquals("User " + userId + " cannot book their own item " + item.getId(),
                itemOwnerBookingException.getMessage());
    }

    @Test
    void getBooking_whenBookingNotFound_thenNotFoundExceptionThrown() {
        long bookingId = 10000L;

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.getBooking(testUser.getId(), bookingId));
        assertEquals("Booking with id " + bookingId + " not found", notFoundException.getMessage());
    }

    @Test
    void getBooking_whenUserNotItemOwnerOrBooker_thenAccessDeniedExceptionThrown() {
        long bookingId = booking.getId();
        long userId = testUser.getId();

        AccessDeniedException accessDeniedException = assertThrows(AccessDeniedException.class,
                () -> bookingService.getBooking(userId, bookingId));
        assertEquals("User " + userId + " is not the owner or Booker of item " + bookingId,
                accessDeniedException.getMessage());
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
        testBooking.setEnd(booking.getEnd().plusDays(1L));
        bookingRepository.save(testBooking);

        List<BookingDto> userBookings = bookingService.getUserBookings(booker.getId(), BookingState.ALL);
        assertEquals(2, userBookings.size());
    }

    @Test
    void getUserBookings_whenBookingStatePast_shouldReturnPastBookings() {
        testBooking.setStart(booking.getStart().minusDays(2L));
        testBooking.setEnd(booking.getStart().minusDays(1L));
        bookingRepository.save(testBooking);

        List<BookingDto> userBookings = bookingService.getUserBookings(booker.getId(), BookingState.PAST);
        assertEquals(1, userBookings.size());
        assertTrue(userBookings.getFirst().getEnd().isBefore(booking.getStart()));
    }

    @Test
    void getUserBookings_whenBookingStateFuture_shouldReturnFutureBookings() {
        testBooking.setStart(booking.getEnd().plusDays(1L));
        testBooking.setEnd(booking.getEnd().plusDays(2L));
        bookingRepository.save(testBooking);

        List<BookingDto> userBookings = bookingService.getUserBookings(booker.getId(), BookingState.FUTURE);
        assertEquals(1, userBookings.size());
        assertTrue(userBookings.getFirst().getStart().isAfter(booking.getEnd()));
    }

    @Test
    void getUserBookings_whenBookingStateWaiting_shouldReturnWaitingBookings() {
        booking.setStatus(BookingStatus.WAITING);
        testBooking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);
        bookingRepository.save(testBooking);

        List<BookingDto> userBookings = bookingService.getUserBookings(booker.getId(), BookingState.WAITING);
        assertEquals(2, userBookings.size());
        for (BookingDto userBooking : userBookings) {
            assertEquals(BookingStatus.WAITING, userBooking.getStatus());
        }
    }

    @Test
    void getUserBookings_whenBookingStateRejected_shouldReturnRejectedBookings() {
        booking.setStatus(BookingStatus.REJECTED);
        testBooking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(booking);
        bookingRepository.save(testBooking);

        List<BookingDto> userBookings = bookingService.getUserBookings(booker.getId(), BookingState.REJECTED);
        assertEquals(2, userBookings.size());
        for (BookingDto userBooking : userBookings) {
            assertEquals(BookingStatus.REJECTED, userBooking.getStatus());
        }
    }

    @Test
    void getUserBookings_whenBookingStateCurrent_shouldReturnCurrentBookings() {
        List<BookingDto> userBookings = bookingService.getUserBookings(booker.getId(), BookingState.CURRENT);
        assertEquals(1, userBookings.size());
        assertTrue(userBookings.getFirst().getStart().isBefore(nowTime));
        assertTrue(userBookings.getFirst().getEnd().isAfter(nowTime));

    }

    @Test
    void getOwnerBooking_whenOwnerItemsNotBooking_shouldReturnEmptyList() {
        List<BookingDto> userBookings = bookingService.getOwnerBookings(booker.getId(), BookingState.ALL);
        assertEquals(0, userBookings.size());
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
        testBooking.setEnd(booking.getEnd().plusDays(1L));
        bookingRepository.save(testBooking);

        List<BookingDto> userBookings = bookingService.getOwnerBookings(owner.getId(), BookingState.ALL);
        assertEquals(2, userBookings.size());
    }

    @Test
    void getOwnerBookings_whenBookingStatePast_shouldReturnPastBookings() {
        testBooking.setStart(booking.getStart().minusDays(2L));
        testBooking.setEnd(booking.getStart().minusDays(1L));
        bookingRepository.save(testBooking);

        List<BookingDto> userBookings = bookingService.getOwnerBookings(owner.getId(), BookingState.PAST);
        assertEquals(1, userBookings.size());
        assertTrue(userBookings.getFirst().getEnd().isBefore(booking.getStart()));
    }

    @Test
    void getOwnerBookings_whenBookingStateFuture_shouldReturnFutureBookings() {
        testBooking.setStart(booking.getEnd().plusDays(1L));
        testBooking.setEnd(booking.getEnd().plusDays(2L));
        bookingRepository.save(testBooking);

        List<BookingDto> userBookings = bookingService.getOwnerBookings(owner.getId(), BookingState.FUTURE);
        assertEquals(1, userBookings.size());
        assertTrue(userBookings.getFirst().getStart().isAfter(booking.getEnd()));
    }

    @Test
    void getOwnerBookings_whenBookingStateWaiting_shouldReturnWaitingBookings() {
        booking.setStatus(BookingStatus.WAITING);
        testBooking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);
        bookingRepository.save(testBooking);

        List<BookingDto> userBookings = bookingService.getOwnerBookings(owner.getId(), BookingState.WAITING);
        assertEquals(2, userBookings.size());
        for (BookingDto userBooking : userBookings) {
            assertEquals(BookingStatus.WAITING, userBooking.getStatus());
        }
    }

    @Test
    void getOwnerBookings_whenBookingStateRejected_shouldReturnRejectedBookings() {
        booking.setStatus(BookingStatus.REJECTED);
        testBooking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(booking);
        bookingRepository.save(testBooking);

        List<BookingDto> userBookings = bookingService.getOwnerBookings(owner.getId(), BookingState.REJECTED);
        assertEquals(2, userBookings.size());
        for (BookingDto userBooking : userBookings) {
            assertEquals(BookingStatus.REJECTED, userBooking.getStatus());
        }
    }

    @Test
    void getOwnerBookings_whenBookingStateCurrent_shouldReturnCurrentBookings() {
        List<BookingDto> userBookings = bookingService.getOwnerBookings(owner.getId(), BookingState.CURRENT);
        assertEquals(1, userBookings.size());
        assertTrue(userBookings.getFirst().getStart().isBefore(nowTime));
        assertTrue(userBookings.getFirst().getEnd().isAfter(nowTime));
    }

    @Test
    void approveBooking_whenBookingNotFound_thenNotFoundExceptionThrown() {
        long bookingId = 10000L;

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.approveBooking(testUser.getId(), bookingId, true));
        assertEquals("Booking with id " + bookingId + " not found", notFoundException.getMessage());
    }

    @Test
    void approveBooking_whenUserNotItemOwner_thenAccessDeniedExceptionThrown() {
        long bookingId = booking.getId();
        long userId = testUser.getId();

        AccessDeniedException accessDeniedException = assertThrows(AccessDeniedException.class,
                () -> bookingService.approveBooking(userId, bookingId, true));
        assertEquals("User " + userId + " is not the owner of item " + item.getId(),
                accessDeniedException.getMessage());
    }

    @Test
    void approveBooking_whenOwnerApprovedBooking_shouldChangedStatus() {
        long bookingId = booking.getId();
        long userId = owner.getId();
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);

        BookingDto bookingDto = bookingService.approveBooking(userId, bookingId, true);
        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());
    }

    @Test
    void approveBooking_whenOwnerRejectedBooking_shouldChangedStatus() {
        long bookingId = booking.getId();
        long userId = owner.getId();

        BookingDto bookingDto = bookingService.approveBooking(userId, bookingId, false);
        assertEquals(BookingStatus.REJECTED, bookingDto.getStatus());
    }
}