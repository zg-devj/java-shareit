package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.UserAlreadyExistException;

import java.util.*;

@Repository
public class UserInMemoryRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private Long identity = 0L;

    @Override
    public User save(User user) {
        isEmailExist(user.getId(), user.getEmail());
        user.setId(++identity);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findUserById(Long userId) {
        return Optional.of(users.get(userId));
    }

    @Override
    public List<User> findAllUsers() {
        return List.copyOf(users.values());
    }

    @Override
    public User update(User user) {
        isEmailExist(user.getId(), user.getEmail());
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public User updateName(Long userId, String name) {
        users.get(userId).setName(name);
        return users.get(userId);
    }

    @Override
    public User updateEmail(Long userId, String email) {
        isEmailExist(userId, email);
        users.get(userId).setEmail(email);
        return users.get(userId);
    }

    @Override
    public void delete(Long userId) {
        users.remove(userId);
    }

    @Override
    public boolean isExistsUser(Long userId) {
        return users.containsKey(userId);
    }

    private void isEmailExist(Long currentUserId, String email) {
        if (users.values().stream()
                .filter(u -> !Objects.equals(u.getId(), currentUserId))
                .anyMatch(e -> e.getEmail().equalsIgnoreCase(email))) {
            throw new UserAlreadyExistException("Пользователь уже существует");
        }
    }
}
