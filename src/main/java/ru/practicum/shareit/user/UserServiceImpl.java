package ru.practicum.shareit.user;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UserAlreadyExistException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    // Добавление пользователя
    @Override
    @Transactional
    public UserDto saveUser(UserDto userDto) {
        User saved = userRepository.save(UserMapper.dtoToUser(userDto));
        log.info("Сохранен пользователь {}", saved.getId());
        return UserMapper.userToDto(saved);
    }

    // Обновление пользователя
    @Override
    @Transactional
    public UserDto updateUser(UserDto userDto) {
        // Если email обновляется и обновляемый email существует у другого
        // пользователя вызвать исключение
        if (userDto.getEmail() != null && userRepository.canNotUpdate(userDto.getId(), userDto.getEmail())) {
            throw new UserAlreadyExistException("Пользователь с таким email существует.");
        }

        // Получаем пользователя для обновления
        User updated = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь c id=%d не найден", userDto.getId())));

        if (userDto.getEmail() != null) {
            log.info("Обновляется email пользователя c id={}.", updated.getId());
            updated.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            log.info("Обновляется имя пользователя c id={}.", updated.getId());
            updated.setName(userDto.getName());
        }
        return UserMapper.userToDto(userRepository.save(updated));
    }

    // Поиск пользователя по id
    @Override
    public UserDto findUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь c id=%d не найден", userId)));
        log.info("Возращен пользовател с id={}.", user.getId());
        return UserMapper.userToDto(user);
    }

    // Удаление пользователя
    @Override
    @Transactional
    public void deleteUser(Long userId) {
        log.info("Удаление пользователя с id={}.", userId);
        userRepository.deleteById(userId);
    }

    // Возврат всех пользователей
    @Override
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        log.info("Возвращено {} пользователей.", users.size());
        return UserMapper.userToDto(users);
    }
}
