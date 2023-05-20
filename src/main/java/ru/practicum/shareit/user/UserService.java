package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto saveUser(UserDto userDto);

    UserDto updateUser(UserDto userDto);

    UserDto findUserById(Long userId);

    void deleteUser(Long userId);

    List<UserDto> findAllUsers();
}
