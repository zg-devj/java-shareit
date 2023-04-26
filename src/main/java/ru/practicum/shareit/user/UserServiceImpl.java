package ru.practicum.shareit.user;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User saveUser(User user) {
        User saved = userRepository.save(user);
        log.info("Сохранен пользователь {}", saved.getId());
        return saved;
    }

    @Override
    public User updateUser(User user) {
        // получаем пользователя
        User updated = userRepository.findUserById(user.getId())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь c id=%d не найден", user.getId())));

        if (user.getName() != null && user.getEmail() == null) {
            log.info("Обновлен email пользователя.");
            return userRepository.updateName(user.getId(), user.getName());
        }
        if (user.getEmail() != null && user.getName() == null) {
            log.info("Обновленно имя пользователя.");
            return userRepository.updateEmail(user.getId(), user.getEmail());
        }

        updated.setName(user.getName());
        updated.setEmail(user.getEmail());
        User updatedUser = userRepository.update(updated);
        log.info("Обновленно имя и email пользователя.");
        return updatedUser;
    }

    @Override
    public User findUserById(Long userId) {
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new NotFoundException(""));
        log.info("Возращен пользовател с id={}.", user.getId());
        return user;
    }

    @Override
    public void deleteUser(Long userId) {
        log.info("Удаление пользователя с id={}.", userId);
        userRepository.delete(userId);
    }

    @Override
    public List<User> findAllUsers() {
        List<User> users = userRepository.findAllUsers();
        log.info("Возвращено {} пользователей.", users.size());
        return users;
    }
}
