package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InvalidException.class)
    public ResponseEntity<Map<String, String>> handleInvalid(final InvalidException e) {
        log.info("Ошибка 400, неверный запрос");
        return new ResponseEntity<>(Map.of("Неверный запрос", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(final DataNotFoundException e) {
        log.info("Ошибка 404, неверный айди");
        return new ResponseEntity<>(Map.of("Ошибка параметра айди", e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, String>> handleValidation(final ValidationException e) {
        log.info("Ошибка 500, ошибка валидации");
        return new ResponseEntity<>(Map.of("Ошибка валидации", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UnsupportedStateException.class)
    protected ResponseEntity<Response> handleUnsupportedStateException() {
        log.info("Ошибка 400, ошибка статуса");
        Response response = new Response("Unknown state: UNSUPPORTED_STATUS");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
