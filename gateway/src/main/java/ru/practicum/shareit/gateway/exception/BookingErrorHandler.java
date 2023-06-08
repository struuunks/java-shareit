package ru.practicum.shareit.gateway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

@RestControllerAdvice
public class BookingErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InvalidException.class)
    public ResponseEntity<Map<String, String>> handleInvalid(final InvalidException e) {
        return new ResponseEntity<>(Map.of("Неверный запрос", e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnsupportedStateException.class)
    public ResponseEntity<Response> handleUnsupportedStateException() {
        Response response = new Response("Unknown state: UNSUPPORTED_STATUS");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Map<String, String>> handleThrowable(final Throwable e) {
        return new ResponseEntity<>(Map.of("Невесрный запрос", e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
