package ru.practicum.ewmmain.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.ewmmain.categories.controller.*;
import ru.practicum.ewmmain.compilation.controller.*;
import ru.practicum.ewmmain.events.controller.*;
import ru.practicum.ewmmain.requests.controller.PrivateRequestController;
import ru.practicum.ewmmain.user.controller.AdminUserController;
import ru.practicum.ewmstatsutil.Formatter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice(assignableTypes = {
        AdminUserController.class,
        AdminCategoryController.class,
        PublicCategoryController.class,
        PrivateEventController.class,
        AdminEventController.class,
        PublicEventController.class,
        PrivateRequestController.class,
        PublicCompilationController.class,
        AdminCompilationController.class})
public class ErrorHandler {

    private void log(Throwable e) {
        log.error("Исключение {}: {}", e, e.getMessage());
    }

    private Map<String, String> createMap(String status, String reason, String message) {
        return Map.of("status", status,
                "reason", reason,
                "message", message,
                "timestamp", LocalDateTime.now().format(Formatter.getFormatter()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValid(final MethodArgumentNotValidException e) {
        log(e);
        List<String> details = new ArrayList<>();
        for (ObjectError error : e.getBindingResult().getAllErrors()) {
            details.add(error.getDefaultMessage());
        }
        return createMap("BAD_REQUEST", "Incorrectly made request", details.get(0));
    }

    @ExceptionHandler({MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            NotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValid(final Exception e) {
        log(e);
        return createMap("BAD_REQUEST", "Incorrectly made request", e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(final NotFoundException e) {
        log(e);
        return createMap("NOT_FOUND", "The required object was not found", e.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleConflict(final ConflictException e) {
        log(e);
        return createMap("CONFLICT", "Integrity constraint has been violated", e.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handleNoAccess(final ForbiddenException e) {
        log(e);
        return createMap("FORBIDDEN", "For the requested operation the conditions are not met.",
                e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleOtherExc(final Exception e) {
        log(e);
        return createMap("INTERNAL_SERVER_ERROR", "Unexpected error", e.getMessage());
    }
}
