package ru.practicum.shareit.user;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UserAlreadyExistException;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User saveUser(User user) {
        Optional<User> savedUser = userRepository.findByEmail(user.getEmail());
        if (savedUser.isPresent()) {
            throw new UserAlreadyExistException("Пользователь уже существует.");
        }
        User saved = userRepository.save(user);
        log.info("Сохранен пользователь {}", saved.getId());
        return saved;
    }

    @Override
    public User updateUser(User user) {
        if (user.getEmail() != null && !userRepository.canUpdate(user.getId(), user.getEmail())) {
            throw new UserAlreadyExistException("Пользователь с таким email существует.");
        }
        User updated = userRepository.findById(user.getId())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь c id=%d не найден", user.getId())));

        if (user.getEmail() != null) {
            log.info("Обновляется email пользователя c id={}.", updated.getId());
            updated.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            log.info("Обновляется имя пользователя c id={}.", updated.getId());
            updated.setName(user.getName());
        }
        return userRepository.update(updated);
    }

    @Override
    public User findUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь c id=%d не найден", userId)));
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
        List<User> users = userRepository.findAll();
        log.info("Возвращено {} пользователей.", users.size());
        return users;
    }
}
