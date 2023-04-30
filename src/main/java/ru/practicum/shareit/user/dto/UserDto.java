package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Builder
@Data
public class UserDto {
    private Long id;

    @NotBlank(message = "Имя не может быть пустым или отсутствовать.")
    @Size(max = 50, message = "Имя не должна быть больше 50 символов.")
    private String name;

    @NotBlank(message = "Email не может быть пустым или отсутствовать.")
    @Email(message = "Некорректный адрес электронной почты")
    private String email;
}
