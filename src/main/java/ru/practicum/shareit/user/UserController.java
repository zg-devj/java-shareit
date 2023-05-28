package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(
            @Valid @RequestBody UserDto userDto
    ) {
        log.info("POST /users - создание пользователя.");
        return userService.saveUser(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto update(
            @PathVariable Long id,
            @RequestBody UserDto userDto
    ) {
        log.info("PATCH /users/{} - обновить пользователя.", id);
        userDto.setId(id);
        return userService.updateUser(userDto);
    }

    @GetMapping("/{id}")
    public UserDto findById(
            @PathVariable Long id
    ) {
        log.info("GET /users/{} - получение пользователя.", id);
        return userService.findUserById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id
    ) {
        log.info("DELETE /users/{} - удалить пользователя.", id);
        userService.deleteUser(id);
    }

    @GetMapping
    public List<UserDto> findAll() {
        log.info("GET /users - вернуть всех пользователей пользователя.");
        return userService.findAllUsers();
    }
}
