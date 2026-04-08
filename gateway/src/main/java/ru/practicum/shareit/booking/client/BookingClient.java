package ru.practicum.shareit.booking.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Slf4j
@Service
public class BookingClient extends BaseClient {

    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    public ResponseEntity<Object> createBooking(long userId, BookingCreateRequestDto bookingCreateRequestDto) {
        log.info("POST /bookings - create Booking {} user with id {}", bookingCreateRequestDto, userId);
        return post("", userId, bookingCreateRequestDto);
    }

    public ResponseEntity<Object> getBooking(long userId, long bookingId) {
        log.info("GET /bookings/{bookingId} with id {}", bookingId);
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getUserBookings(long userId, BookingState state) {
        Map<String, Object> parameters = Map.of("state", state.name());
        log.info("GET /bookings user with id {}", userId);
        return get("?state={state}", userId, parameters);
    }

    public ResponseEntity<Object> getOwnerBookings(long userId, BookingState state) {
        Map<String, Object> parameters = Map.of("state", state.name());
        log.info("GET /bookings/owner user with id {}", userId);
        return get("/owner?state={state}", userId, parameters);
    }

    public ResponseEntity<Object> approveBooking(long userId, long bookingId, boolean approved) {
        Map<String, Object> parameters = Map.of("approved", approved);
        log.info("PATCH /bookings/{} approved {}", bookingId, approved);
        return patch("/" + bookingId + "?approved={approved}", userId, parameters, null);
    }
}