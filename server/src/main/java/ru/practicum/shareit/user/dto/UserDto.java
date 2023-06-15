package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserDto {
    private Long id;

    // TODO: 12.06.2023 DELETE
//    @NotBlank
//    @Size(max = 50)
    private String name;

    // TODO: 12.06.2023 DELETE
//    @NotBlank
//    @Email
//    @Size(max = 512)
    private String email;
}
