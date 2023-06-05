package ru.practicum.shareit.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
