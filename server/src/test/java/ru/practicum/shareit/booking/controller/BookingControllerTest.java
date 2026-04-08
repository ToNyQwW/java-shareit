package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.util.RequestHeaderConstants.USER_ID_HEADER;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @Test
    @SneakyThrows
    void createBooking_shouldReturnCreatedBooking() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingCreateDto requestDto = BookingCreateDto.builder()
                .start(start)
                .end(end)
                .build();

        BookingDto responseDto = BookingDto.builder()
                .id(1L)
                .start(start)
                .end(end)
                .status(BookingStatus.WAITING)
                .build();

        when(bookingService.createBooking(eq(1L), any(BookingCreateDto.class)))
                .thenReturn(responseDto);

        String expectedStart = objectMapper.writeValueAsString(start).replace("\"", "");
        String expectedEnd = objectMapper.writeValueAsString(end).replace("\"", "");

        mockMvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, 1L)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.start", is(expectedStart)))
                .andExpect(jsonPath("$.end", is(expectedEnd)))
                .andExpect(jsonPath("$.status", is(BookingStatus.WAITING.toString())));
    }

    @Test
    @SneakyThrows
    void getBooking_shouldReturnBooking() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingDto responseDto = BookingDto.builder()
                .id(1L)
                .start(start)
                .end(end)
                .status(BookingStatus.APPROVED)
                .build();

        when(bookingService.getBooking(1L, 1L)).thenReturn(responseDto);

        String expectedStart = objectMapper.writeValueAsString(start).replace("\"", "");
        String expectedEnd = objectMapper.writeValueAsString(end).replace("\"", "");

        mockMvc.perform(get("/bookings/1")
                        .header(USER_ID_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.start", is(expectedStart)))
                .andExpect(jsonPath("$.end", is(expectedEnd)))
                .andExpect(jsonPath("$.status", is(BookingStatus.APPROVED.toString())));
    }

    @Test
    @SneakyThrows
    void getUserBookings_shouldReturnList() {
        LocalDateTime start1 = LocalDateTime.now().plusDays(1);
        LocalDateTime end1 = LocalDateTime.now().plusDays(2);

        LocalDateTime start2 = LocalDateTime.now().plusDays(3);
        LocalDateTime end2 = LocalDateTime.now().plusDays(4);

        BookingDto booking1 = BookingDto.builder()
                .id(1L)
                .start(start1)
                .end(end1)
                .status(BookingStatus.WAITING)
                .build();

        BookingDto booking2 = BookingDto.builder()
                .id(2L)
                .start(start2)
                .end(end2)
                .status(BookingStatus.REJECTED)
                .build();

        when(bookingService.getUserBookings(1L, BookingState.ALL))
                .thenReturn(List.of(booking1, booking2));

        mockMvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, 1L)
                        .param("state", "ALL")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].status", is(BookingStatus.WAITING.toString())))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].status", is(BookingStatus.REJECTED.toString())));
    }

    @Test
    @SneakyThrows
    void getOwnerBookings_shouldReturnList() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingDto booking = BookingDto.builder()
                .id(1L)
                .start(start)
                .end(end)
                .status(BookingStatus.APPROVED)
                .build();

        when(bookingService.getOwnerBookings(1L, BookingState.ALL))
                .thenReturn(List.of(booking));

        String expectedStart = objectMapper.writeValueAsString(start).replace("\"", "");
        String expectedEnd = objectMapper.writeValueAsString(end).replace("\"", "");

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, 1L)
                        .param("state", "ALL")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].start", is(expectedStart)))
                .andExpect(jsonPath("$[0].end", is(expectedEnd)))
                .andExpect(jsonPath("$[0].status", is(BookingStatus.APPROVED.toString())));
    }

    @Test
    @SneakyThrows
    void approveBooking_shouldReturnApprovedBooking() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingDto responseDto = BookingDto.builder()
                .id(1L)
                .start(start)
                .end(end)
                .status(BookingStatus.APPROVED)
                .build();

        when(bookingService.approveBooking(1L, 1L, true))
                .thenReturn(responseDto);

        String expectedStart = objectMapper.writeValueAsString(start).replace("\"", "");
        String expectedEnd = objectMapper.writeValueAsString(end).replace("\"", "");

        mockMvc.perform(patch("/bookings/1")
                        .header(USER_ID_HEADER, 1L)
                        .param("approved", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.start", is(expectedStart)))
                .andExpect(jsonPath("$.end", is(expectedEnd)))
                .andExpect(jsonPath("$.status", is(BookingStatus.APPROVED.toString())));
    }
}