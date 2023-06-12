package ru.practicum.shareit.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exceptions.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    // 400
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handlerValidateException(final BadRequestException e) {
        log.warn(e.getMessage());
        return new ErrorMessage(e.getMessage());
    }

    // 400
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ErrorMessageField> handlerValidateException(final MethodArgumentNotValidException e) {
        log.warn(e.getMessage());
        List<ErrorMessageField> errors = new ArrayList<>();
        //StringBuilder builder = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(
                fieldError -> {
                    errors.add(new ErrorMessageField(fieldError.getField(), fieldError.getDefaultMessage()));
                    //builder.append();
                });
        return errors;
    }

    // 403
    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorMessage handleForbiddenException(final ForbiddenException e) {
        log.warn(e.getMessage());
        return new ErrorMessage(e.getMessage());
    }

    // 404
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage handleNotFoundException(final NotFoundException e) {
        log.warn(e.getMessage());
        return new ErrorMessage(e.getMessage());
    }

    // 409
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorMessage handleUserAlreadyExistException(final UserAlreadyExistException e) {
        log.warn(e.getMessage());
        return new ErrorMessage(e.getMessage());
    }

    // 500
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handlerException(final RuntimeException e) {
        String msg = "Произошла непредвиденная ошибка.";
        log.warn(msg);
        return new ErrorMessage(msg);
    }
}
