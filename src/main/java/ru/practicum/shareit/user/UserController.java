package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

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
        User user = UserMapper.toUser(userDto);
        User created = userService.saveUser(user);
        response.setStatus(201);
        return UserMapper.toUserDto(created);
    }

    @PatchMapping("/{id}")
    public UserDto update(
            @PathVariable Long id,
            @RequestBody UserDto userDto
    ) {
        log.info("PATCH /users/{} - обновить пользователя", id);
        userDto.setId(id);
        User user = UserMapper.toUser(userDto);
        User updated = userService.updateUser(user);
        return UserMapper.toUserDto(updated);
    }

    @GetMapping("/{id}")
    public UserDto findById(
            @PathVariable Long id
    ) {
        log.info("GET /users/{} - получение пользователя", id);
        return UserMapper.toUserDto(userService.findUserById(id));
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
        List<User> users = userService.findAllUsers();
        return users.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }
}
