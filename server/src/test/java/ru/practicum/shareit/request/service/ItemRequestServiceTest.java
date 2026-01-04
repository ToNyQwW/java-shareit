package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class ItemRequestServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestService itemRequestService;


    private User owner;
    private User booker;
    private ItemRequestCreateDto requestCreateDto;

    @BeforeEach
    public void setUp() {
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

        requestCreateDto = ItemRequestCreateDto.builder()
                .description("description")
                .build();
    }

    @Test
    void createItemRequest_whenUserNotFound_thenNotFoundExceptionThrown() {
        long userId = 10000L;

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemRequestService.createItemRequest(userId, requestCreateDto));
        assertEquals("User with id " + userId + " not found", notFoundException.getMessage());
    }

    @Test
    void createItemRequest_shouldCorrectlyCreateItemRequest() {
        long userId = booker.getId();

        ItemRequestDto itemRequest = itemRequestService.createItemRequest(userId, requestCreateDto);

        assertEquals(itemRequest.getRequestorId(), userId);
        assertEquals(requestCreateDto.getDescription(), itemRequest.getDescription());
    }

    @Test
    void getItemRequest_whenRequestNotFound_thenNotFoundExceptionThrown() {
        long userId = 10000L;

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequest(userId));
        assertEquals("ItemRequest with id " + userId + " not found", notFoundException.getMessage());
    }

    @Test
    void getItemRequest_shouldCorrectlyGetItemRequest() {
        ItemRequestDto created = itemRequestService.createItemRequest(booker.getId(), requestCreateDto);
        ItemRequestWithItemsDto result = itemRequestService.getItemRequest(created.getId());

        assertEquals(created.getId(), result.getId());
        assertEquals(created.getDescription(), result.getDescription());
        assertTrue(result.getItems().isEmpty());
    }

    @Test
    void getAllItemRequests_shouldCorrectlyGetAllItemRequests() {
        itemRequestService.createItemRequest(booker.getId(), requestCreateDto);
        List<ItemRequestDto> requests = itemRequestService.getAllItemRequests(owner.getId());

        assertEquals(1, requests.size());
        assertEquals(requestCreateDto.getDescription(), requests.get(0).getDescription());
    }

    @Test
    void getAllItemRequests_whenRequestNotFound_thenNotFoundExceptionThrown() {
        long userId = 10000L;

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequest(userId));
        assertEquals("ItemRequest with id " + userId + " not found", notFoundException.getMessage());
    }

    @Test
    void getUserItemRequests_whenRequestNotFound_thenNotFoundExceptionThrown() {
        long userId = 10000L;

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequest(userId));
        assertEquals("ItemRequest with id " + userId + " not found", notFoundException.getMessage());
    }

    @Test
    void getUserItemRequests_shouldCorrectlyGetUserItemRequest() {
        itemRequestService.createItemRequest(booker.getId(), requestCreateDto);
        List<ItemRequestWithItemsDto> requests = itemRequestService.getUserItemRequests(booker.getId());

        assertEquals(1, requests.size());
        assertEquals(requestCreateDto.getDescription(), requests.get(0).getDescription());
    }
}