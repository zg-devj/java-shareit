package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto create(
            @Valid @RequestBody UserDto userDto,
            HttpServletResponse response
    ) {
        log.info("POST /users - создание пользователя");
        UserDto created = userService.saveUser(userDto);
        response.setStatus(201);
        return created;
    }

    @PatchMapping("/{id}")
    public UserDto update(
            @PathVariable Long id,
            @RequestBody UserDto userDto
    ) {
        log.info("PATCH /users/{} - обновить пользователя", id);
        userDto.setId(id);
        return userService.updateUser(userDto);
    }

    @GetMapping("/{id}")
    public UserDto findById(
            @PathVariable Long id
    ) {
        log.info("GET /users/{} - получение пользователя", id);
        return userService.findUserById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id,
            HttpServletResponse response
    ) {
        log.info("DELETE /users/{} - удалить пользователя", id);
        userService.deleteUser(id);
        response.setStatus(204);
    }

    @GetMapping
    public List<UserDto> findAll() {
        log.info("GET /users - вернуть всех пользователей пользователя");
        return userService.findAllUsers();
    }
}
