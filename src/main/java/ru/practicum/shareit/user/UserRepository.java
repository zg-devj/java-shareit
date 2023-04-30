package ru.practicum.shareit.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);

    Optional<User> findById(Long userId);

    Optional<User> findByEmail(String email);

    List<User> findAll();

    User update(User user);

    void delete(Long userId);

    boolean existsById(Long userId);

    boolean canUpdate(Long userId, String email);
}
