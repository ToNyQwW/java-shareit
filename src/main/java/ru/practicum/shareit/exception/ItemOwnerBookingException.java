package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ItemOwnerBookingException extends RuntimeException {
    public ItemOwnerBookingException(String message) {
        super(message);
    }
}