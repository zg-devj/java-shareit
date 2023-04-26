package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Builder
@Getter
public class UserDto {
    private Long id;

    @NotBlank(message = "Имя не может быть пустым или отсутствовать.")
    @Size(max = 50, message = "Имя не должна быть больше 50 символов.")
    private String name;

    @NotBlank(message = "Email не может быть пустым или отсутствовать.")
    @Email
    private String email;
}
