package ru.practicum.shareit.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
public class ErrorMessageField extends ErrorMessage {

    private String field;

    public ErrorMessageField(String field, String error) {
        super(error);
        this.field = field;
    }
}
