package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    User saveUser(User user);

    User updateUser(User user);

    User findUserById(Long userId);

    void deleteUser(Long userId);

    List<User> findAllUsers();
}
