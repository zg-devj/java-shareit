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
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) {
        // получаем пользователя
        User updated = userRepository.findUserById(user.getId())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь c id=%d не найден", user.getId())));

        if (user.getName() != null && user.getEmail() == null) {
            return userRepository.updateName(user.getId(), user.getName());
        }
        if (user.getEmail() != null && user.getName() == null) {
            return userRepository.updateEmail(user.getId(), user.getEmail());
        }

        updated.setName(user.getName());
        updated.setEmail(user.getEmail());
        return userRepository.update(updated);
    }

    @Override
    public User findUserById(Long userId) {
        return userRepository.findUserById(userId)
                .orElseThrow(() -> new NotFoundException(""));
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.delete(userId);
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAllUsers();
    }
}
