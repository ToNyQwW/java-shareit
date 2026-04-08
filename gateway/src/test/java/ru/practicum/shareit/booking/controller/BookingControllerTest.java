package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingClient bookingClient;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @Test
    void shouldCreateBooking() throws Exception {
        BookingCreateRequestDto dto = BookingCreateRequestDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        Mockito.when(bookingClient.createBooking(anyLong(), any()))
                .thenReturn(ResponseEntity.ok("Created"));

        mockMvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Created"));
    }

    @Test
    void shouldFailCreateBookingWithInvalidDates() throws Exception {
        BookingCreateRequestDto dto = BookingCreateRequestDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().minusHours(1))
                .build();

        mockMvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetBookingById() throws Exception {
        Mockito.when(bookingClient.getBooking(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok("Booking info"));

        mockMvc.perform(get("/bookings/10")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("Booking info"));
    }

    @Test
    void shouldGetUserBookingsWithDefaultState() throws Exception {
        Mockito.when(bookingClient.getUserBookings(anyLong(), any()))
                .thenReturn(ResponseEntity.ok("User bookings"));

        mockMvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, 2L))
                .andExpect(status().isOk())
                .andExpect(content().string("User bookings"));
    }

    @Test
    void shouldGetOwnerBookingsWithStateParam() throws Exception {
        Mockito.when(bookingClient.getOwnerBookings(anyLong(), any()))
                .thenReturn(ResponseEntity.ok("Owner bookings"));

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, 2L)
                        .param("state", "WAITING"))
                .andExpect(status().isOk())
                .andExpect(content().string("Owner bookings"));
    }

    @Test
    void shouldApproveBooking() throws Exception {
        Mockito.when(bookingClient.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(ResponseEntity.ok("Approved"));

        mockMvc.perform(patch("/bookings/5")
                        .header(USER_ID_HEADER, 3L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string("Approved"));
    }
}