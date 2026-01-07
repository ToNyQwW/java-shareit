package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItemRequestServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestService itemRequestService;

    @BeforeAll
    void setUp() {
        userRepository.save(
                User.builder()
                        .name("owner")
                        .email("owner@test.com")
                        .build()
        );

        userRepository.save(
                User.builder()
                        .name("booker")
                        .email("booker@test.com")
                        .build()
        );
    }

    @Test
    void createItemRequest_whenUserNotFound_thenNotFoundExceptionThrown() {
        long userId = 10000L;
        ItemRequestCreateDto requestCreateDto = ItemRequestCreateDto.builder()
                .description("description")
                .build();

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemRequestService.createItemRequest(userId, requestCreateDto));
        assertEquals("User with id " + userId + " not found", notFoundException.getMessage());
    }

    @Test
    void createItemRequest_shouldCorrectlyCreateItemRequest() {
        User booker = userRepository.findById(2L).orElseThrow();
        ItemRequestCreateDto requestCreateDto = ItemRequestCreateDto.builder()
                .description("description")
                .build();

        ItemRequestDto itemRequest = itemRequestService.createItemRequest(booker.getId(), requestCreateDto);

        assertEquals(itemRequest.getRequestorId(), booker.getId());
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
        User booker = userRepository.findById(2L).orElseThrow();
        ItemRequestCreateDto requestCreateDto = ItemRequestCreateDto.builder()
                .description("description")
                .build();

        ItemRequestDto createdRequest = itemRequestService.createItemRequest(booker.getId(), requestCreateDto);
        ItemRequestWithItemsDto result = itemRequestService.getItemRequest(createdRequest.getId());

        assertEquals(createdRequest.getId(), result.getId());
        assertEquals(createdRequest.getDescription(), result.getDescription());
        assertTrue(result.getItems().isEmpty());
    }

    @Test
    void getAllItemRequests_shouldCorrectlyGetAllItemRequests() {
        User booker = userRepository.findById(2L).orElseThrow();
        ItemRequestCreateDto requestCreateDto = ItemRequestCreateDto.builder()
                .description("description")
                .build();

        itemRequestService.createItemRequest(booker.getId(), requestCreateDto);

        User owner = userRepository.findById(1L).orElseThrow();
        List<ItemRequestDto> requests = itemRequestService.getAllItemRequests(owner.getId());

        assertEquals(1, requests.size());
        assertEquals(requestCreateDto.getDescription(), requests.getFirst().getDescription());
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
        User booker = userRepository.findById(2L).orElseThrow();
        ItemRequestCreateDto requestCreateDto = ItemRequestCreateDto.builder()
                .description("description")
                .build();

        itemRequestService.createItemRequest(booker.getId(), requestCreateDto);

        List<ItemRequestWithItemsDto> requests = itemRequestService.getUserItemRequests(booker.getId());

        assertEquals(1, requests.size());
        assertEquals(requestCreateDto.getDescription(), requests.getFirst().getDescription());
    }
}