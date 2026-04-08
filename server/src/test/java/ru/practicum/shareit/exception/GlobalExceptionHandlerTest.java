package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.service.ItemRequestService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {BookingController.class, ItemController.class, ItemRequestController.class})
@Import(GlobalExceptionHandler.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private ItemService itemService;

    @MockBean
    private ItemRequestService itemRequestService;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @Test
    void shouldHandleNotFoundException() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenThrow(new NotFoundException("Бронирование не найдено"));

        mockMvc.perform(get("/bookings/999")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Бронирование не найдено"));
    }

    @Test
    void shouldHandleAccessDeniedException() throws Exception {
        when(itemService.updateItem(anyLong(), anyLong(), any()))
                .thenThrow(new AccessDeniedException("Доступ запрещён"));

        mockMvc.perform(patch("/items/1")
                        .header(USER_ID_HEADER, 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Доступ запрещён"));
    }

    @Test
    void shouldHandleBadRequestException() throws Exception {
        when(itemService.createComment(anyLong(), anyLong(), any()))
                .thenThrow(new BookingNotCompletedException("Бронирование не завершено"));

        mockMvc.perform(post("/items/1/comment")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Бронирование не завершено"));
    }


    @Test
    void shouldHandleDuplicateEmailException() throws Exception {
        when(itemRequestService.createItemRequest(anyLong(), any()))
                .thenThrow(new DuplicateEmailException("Email уже используется"));

        mockMvc.perform(post("/requests")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Email уже используется"));
    }
}