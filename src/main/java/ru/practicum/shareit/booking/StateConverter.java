package ru.practicum.shareit.booking;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StateConverter implements Converter<String, State> {
    @Override
    public State convert(String source) {
        try {
            return State.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
