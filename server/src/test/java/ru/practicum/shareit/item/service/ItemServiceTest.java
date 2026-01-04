package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.BookingNotCompletedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class ItemServiceTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private Item item;
    private User owner;
    private User booker;
    private Booking booking;
    private LocalDateTime now;
    private ItemCreateDto itemCreateDto;
    private ItemUpdateDto itemUpdateDto;
    private CommentCreateDto commentCreateDto;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

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

        item = Item.builder()
                .name("item")
                .description("itemDescription")
                .available(true)
                .owner(owner)
                .build();
        itemRepository.save(item);

        booking = Booking.builder()
                .item(item)
                .booker(booker)
                .start(now.plusDays(1))
                .end(now.plusDays(3))
                .status(BookingStatus.APPROVED)
                .build();
        bookingRepository.save(booking);

        itemCreateDto = ItemCreateDto.builder()
                .name("itemDto")
                .description("itemDtoDescription")
                .available(true)
                .build();

        itemUpdateDto = ItemUpdateDto.builder()
                .description("updatedDescription")
                .build();

        commentCreateDto = CommentCreateDto.builder()
                .text("Comment")
                .build();
    }

    @Test
    void createItem_whenUserNotFound_thenNotFoundExceptionThrown() {
        long userId = 10000L;

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemService.createItem(userId, itemCreateDto));
        assertEquals("User with id " + userId + " not found", notFoundException.getMessage());
    }

    @Test
    void createItem_whenRequestIdIsNotNullAndNotFound_thenNotFoundExceptionThrown() {
        long userId = booker.getId();
        long requestId = 10000L;
        itemCreateDto.setRequestId(requestId);

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemService.createItem(userId, itemCreateDto));
        assertEquals("ItemRequest with id " + requestId + " not found", notFoundException.getMessage());
    }

    @Test
    void getItem_whenUserIsOwner_shouldLastAndNextBookingNotNull() {
        long userId = owner.getId();
        long itemId = item.getId();
        Booking lastBooking = Booking.builder()
                .item(item)
                .booker(booker)
                .start(now.minusDays(3))
                .end(now.minusDays(1))
                .status(BookingStatus.APPROVED)
                .build();
        bookingRepository.save(lastBooking);

        ItemWithAdditionalInfoDto result = itemService.getItem(userId, itemId);
        assertNotNull(result.getLastBooking());
        assertNotNull(result.getNextBooking());
        assertEquals(result.getLastBooking().getItem().getId(), itemId);
        assertEquals(result.getNextBooking().getItem().getId(), itemId);
    }

    @Test
    void getItem_whenUserIsNotOwner_shouldLastAndNextBookingNull() {
        long userId = booker.getId();
        long itemId = item.getId();
        Booking nextBooking = Booking.builder()
                .item(item)
                .booker(booker)
                .start(now.plusDays(1))
                .end(now.plusDays(3))
                .status(BookingStatus.APPROVED)
                .build();
        bookingRepository.save(nextBooking);

        ItemWithAdditionalInfoDto result = itemService.getItem(userId, itemId);
        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
    }

    @Test
    void searchItems_whenSearchIsBlank_shouldReturnEmptyList() {
        List<ItemDto> result = itemService.searchItems("");

        assertTrue(result.isEmpty());
    }

    @Test
    void searchItems_shouldReturnItemByName() {
        List<ItemDto> result = itemService.searchItems(item.getName());

        assertFalse(result.isEmpty());
        assertEquals(item.getName(), result.getFirst().getName());
    }

    @Test
    void searchItems_shouldReturnItemByDescription() {
        List<ItemDto> result = itemService.searchItems(item.getDescription());

        assertFalse(result.isEmpty());
        assertEquals(item.getDescription(), result.getFirst().getDescription());
    }

    @Test
    void searchItems_whenSearchIsNotFound_shouldReturnEmptyList() {
        List<ItemDto> result = itemService.searchItems("NotFoundThis");

        assertTrue(result.isEmpty());
    }

    @Test
    void getUserItems_whenUserNotFound_thenNotFoundExceptionThrown() {
        long userId = 10000L;

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemService.getUserItems(userId));
        assertEquals("User with id " + userId + " not found", notFoundException.getMessage());
    }

    @Test
    void getUserItems_whenUserDontHaveItem_shouldReturnEmptyList() {
        long userId = booker.getId();

        List<ItemWithAdditionalInfoDto> userItems = itemService.getUserItems(userId);
        assertTrue(userItems.isEmpty());
    }

    @Test
    void getUserItems_shouldCorrectlyReturnItems() {
        long userId = owner.getId();

        List<ItemWithAdditionalInfoDto> userItems = itemService.getUserItems(userId);
        assertFalse(userItems.isEmpty());

        ItemWithAdditionalInfoDto resultItem = userItems.getFirst();
        assertEquals(resultItem.getId(), item.getId());
        assertNotNull(resultItem.getNextBooking());
        assertNull(resultItem.getLastBooking());
    }

    @Test
    void updateItem_whenUserNotFound_thenNotFoundExceptionThrown() {
        long userId = 10000L;

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(userId, item.getId(), itemUpdateDto));
        assertEquals("User with id " + userId + " not found", notFoundException.getMessage());
    }

    @Test
    void updateItem_whenItemNotFound_thenNotFoundExceptionThrown() {
        long itemId = 10000L;

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(booker.getId(), itemId, itemUpdateDto));
        assertEquals("Item with id " + itemId + " not found", notFoundException.getMessage());
    }

    @Test
    void updateItem_whenUserNotItemOwner_thenAccessDeniedExceptionThrown() {
        long userId = booker.getId();

        AccessDeniedException accessDeniedException = assertThrows(AccessDeniedException.class,
                () -> itemService.updateItem(userId, item.getId(), itemUpdateDto));
        assertEquals("User " + userId + " is not the owner of item " + item.getId(),
                accessDeniedException.getMessage());
    }

    @Test
    void updateItem_shouldCorrectlyUpdateItem() {
        long userId = owner.getId();
        long itemId = item.getId();

        ItemDto itemDto = itemService.updateItem(userId, itemId, itemUpdateDto);

        assertEquals(itemId, itemDto.getId());
        assertEquals(itemUpdateDto.getDescription(), itemDto.getDescription());
    }

    @Test
    void createComment_whenUserNotFound_thenNotFoundExceptionThrown() {
        long userId = 10000L;

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemService.createComment(userId, item.getId(), commentCreateDto));
        assertEquals("User with id " + userId + " not found", notFoundException.getMessage());
    }

    @Test
    void createComment_whenItemNotFound_thenNotFoundExceptionThrown() {
        long itemId = 10000L;

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemService.createComment(booker.getId(), itemId, commentCreateDto));
        assertEquals("Item with id " + itemId + " not found", notFoundException.getMessage());
    }

    @Test
    void createComment_WhenNotHaveCompletedBooking_thenBookingNotCompletedExceptionThrown() {
        BookingNotCompletedException exception = assertThrows(BookingNotCompletedException.class,
                () -> itemService.createComment(booker.getId(), item.getId(), commentCreateDto));
        assertEquals("Completed booking with id: " + item.getId() + " not found", exception.getMessage());
    }

    @Test
    void createComment_shouldCorrectlyCreateComment() {
        Booking lastBooking = Booking.builder()
                .item(item)
                .booker(booker)
                .start(now.minusDays(10))
                .end(now.minusDays(5))
                .status(BookingStatus.APPROVED)
                .build();
        bookingRepository.save(lastBooking);

        CommentDto comment = itemService.createComment(booker.getId(), item.getId(), commentCreateDto);

        assertEquals(booker.getName(), comment.getAuthorName());
        assertEquals(commentCreateDto.getText(), comment.getText());
    }

}