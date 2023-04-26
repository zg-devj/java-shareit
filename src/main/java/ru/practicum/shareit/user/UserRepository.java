package ru.practicum.shareit.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);

    Optional<User> findUserById(Long userId);

    List<User> findAllUsers();

    User update(User user);

    User updateName(Long userId, String name);

    User updateEmail(Long userId, String email);

    void delete(Long userId);

    boolean isExistsUser(Long userId);
}
