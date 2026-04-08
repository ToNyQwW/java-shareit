package ru.practicum.shareit.item.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
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
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = AFTER_CLASS)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItemServiceTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @BeforeAll
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        User owner = userRepository.save(User.builder()
                .name("owner")
                .email("owner@test.com")
                .build());

        User booker = userRepository.save(User.builder()
                .name("booker")
                .email("booker@test.com")
                .build());

        Item item = itemRepository.save(
                Item.builder()
                        .name("item")
                        .description("description")
                        .available(true)
                        .owner(owner)
                        .build()
        );

        bookingRepository.save(
                Booking.builder()
                        .item(item)
                        .booker(booker)
                        .start(now.minusDays(3))
                        .end(now.minusDays(1))
                        .status(BookingStatus.APPROVED)
                        .build()
        );

        bookingRepository.save(
                Booking.builder()
                        .item(item)
                        .booker(booker)
                        .start(now.plusDays(1))
                        .end(now.plusDays(3))
                        .status(BookingStatus.APPROVED)
                        .build()
        );
    }

    @Test
    void createItem_whenUserNotFound_thenNotFoundExceptionThrown() {
        long userId = 10000L;
        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name("itemCreateDto")
                .description("itemCreateDtoDescription")
                .available(true)
                .build();

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemService.createItem(userId, itemCreateDto));
        assertEquals("User with id " + userId + " not found", notFoundException.getMessage());
    }

    @Test
    void createItem_whenRequestIdIsNotNullAndNotFound_thenNotFoundExceptionThrown() {
        User owner = userRepository.findById(1L).orElseThrow();
        long requestId = 10000L;
        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name("itemCreateDto")
                .description("itemCreateDtoDescription")
                .available(true)
                .requestId(requestId)
                .build();

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemService.createItem(owner.getId(), itemCreateDto));
        assertEquals("ItemRequest with id " + requestId + " not found", notFoundException.getMessage());
    }

    @Test
    void getItem_whenUserIsOwner_shouldLastAndNextBookingNotNull() {
        User owner = userRepository.findById(1L).orElseThrow();
        Item item = itemRepository.findById(1L).orElseThrow();

        ItemWithAdditionalInfoDto result = itemService.getItem(owner.getId(), item.getId());
        assertNotNull(result.getLastBooking());
        assertNotNull(result.getNextBooking());
        assertEquals(item.getId(), result.getLastBooking().getItem().getId());
        assertEquals(item.getId(), result.getNextBooking().getItem().getId());
    }

    @Test
    void getItem_whenUserIsNotOwner_shouldLastAndNextBookingNull() {
        User booker = userRepository.findById(2L).orElseThrow();
        Item item = itemRepository.findById(1L).orElseThrow();

        ItemWithAdditionalInfoDto result = itemService.getItem(booker.getId(), item.getId());
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
        Item item = itemRepository.findById(1L).orElseThrow();
        List<ItemDto> result = itemService.searchItems(item.getName());

        assertFalse(result.isEmpty());
        assertEquals(item.getName(), result.getFirst().getName());
    }

    @Test
    void searchItems_shouldReturnItemByDescription() {
        Item item = itemRepository.findById(1L).orElseThrow();
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
    void getUserItems_whenUserDoesNotHaveItems_shouldReturnEmptyList() {
        User booker = userRepository.findById(2L).orElseThrow();

        List<ItemWithAdditionalInfoDto> userItems = itemService.getUserItems(booker.getId());
        assertTrue(userItems.isEmpty());
    }

    @Test
    void getUserItems_shouldCorrectlyReturnItems() {
        User owner = userRepository.findById(1L).orElseThrow();
        Item item = itemRepository.findById(1L).orElseThrow();

        List<ItemWithAdditionalInfoDto> result = itemService.getUserItems(owner.getId());
        assertFalse(result.isEmpty());

        ItemWithAdditionalInfoDto firstItem = result.getFirst();
        assertEquals(item.getId(), firstItem.getId());
        assertNotNull(firstItem.getNextBooking());
        assertNotNull(firstItem.getLastBooking());
    }

    @Test
    void updateItem_whenUserNotFound_thenNotFoundExceptionThrown() {
        long userId = 10000L;
        Item item = itemRepository.findById(1L).orElseThrow();
        ItemUpdateDto itemUpdateDto = ItemUpdateDto.builder()
                .description("updatedDescription")
                .build();

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(userId, item.getId(), itemUpdateDto));
        assertEquals("User with id " + userId + " not found", notFoundException.getMessage());
    }

    @Test
    void updateItem_whenItemNotFound_thenNotFoundExceptionThrown() {
        long itemId = 10000L;
        User booker = userRepository.findById(2L).orElseThrow();
        ItemUpdateDto itemUpdateDto = ItemUpdateDto.builder()
                .description("updatedDescription")
                .build();

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(booker.getId(), itemId, itemUpdateDto));
        assertEquals("Item with id " + itemId + " not found", notFoundException.getMessage());
    }

    @Test
    void updateItem_whenUserNotItemOwner_thenAccessDeniedExceptionThrown() {
        User booker = userRepository.findById(2L).orElseThrow();
        Item item = itemRepository.findById(1L).orElseThrow();
        ItemUpdateDto itemUpdateDto = ItemUpdateDto.builder()
                .description("updatedDescription")
                .build();

        AccessDeniedException accessDeniedException = assertThrows(AccessDeniedException.class,
                () -> itemService.updateItem(booker.getId(), item.getId(), itemUpdateDto));
        assertEquals("User " + booker.getId() + " is not the owner of item " + item.getId(),
                accessDeniedException.getMessage());
    }

    @Test
    void updateItem_shouldCorrectlyUpdateItem() {
        User owner = userRepository.findById(1L).orElseThrow();
        Item item = itemRepository.findById(1L).orElseThrow();
        ItemUpdateDto itemUpdateDto = ItemUpdateDto.builder()
                .description("updatedDescription")
                .build();

        ItemDto updatedItem = itemService.updateItem(owner.getId(), item.getId(), itemUpdateDto);

        assertEquals(item.getId(), updatedItem.getId());
        assertEquals(itemUpdateDto.getDescription(), updatedItem.getDescription());
    }

    @Test
    void createComment_whenUserNotFound_thenNotFoundExceptionThrown() {
        long userId = 10000L;
        Item item = itemRepository.findById(1L).orElseThrow();
        CommentCreateDto commentCreateDto = CommentCreateDto.builder()
                .text("Comment")
                .build();

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemService.createComment(userId, item.getId(), commentCreateDto));
        assertEquals("User with id " + userId + " not found", notFoundException.getMessage());
    }

    @Test
    void createComment_whenItemNotFound_thenNotFoundExceptionThrown() {
        long itemId = 10000L;
        User booker = userRepository.findById(2L).orElseThrow();
        CommentCreateDto commentCreateDto = CommentCreateDto.builder()
                .text("Comment")
                .build();

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemService.createComment(booker.getId(), itemId, commentCreateDto));
        assertEquals("Item with id " + itemId + " not found", notFoundException.getMessage());
    }

    @Test
    void createComment_whenNoCompletedBooking_thenBookingNotCompletedExceptionThrown() {
        User owner = userRepository.findById(1L).orElseThrow();
        Item item = itemRepository.findById(1L).orElseThrow();
        CommentCreateDto commentCreateDto = CommentCreateDto.builder()
                .text("Comment")
                .build();

        BookingNotCompletedException exception = assertThrows(BookingNotCompletedException.class,
                () -> itemService.createComment(owner.getId(), item.getId(), commentCreateDto));
        assertEquals("Completed booking with id: " + item.getId() + " not found", exception.getMessage());
    }

    @Test
    void createComment_shouldCorrectlyCreateComment() {
        User booker = userRepository.findById(2L).orElseThrow();
        Item item = itemRepository.findById(1L).orElseThrow();
        CommentCreateDto commentCreateDto = CommentCreateDto.builder()
                .text("Comment")
                .build();

        CommentDto createdComment = itemService.createComment(booker.getId(), item.getId(), commentCreateDto);
        assertEquals(booker.getName(), createdComment.getAuthorName());
        assertEquals(commentCreateDto.getText(), createdComment.getText());
    }
}