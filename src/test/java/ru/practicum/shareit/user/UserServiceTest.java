package ru.practicum.shareit.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UserAlreadyExistException;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("user")
                .email("user@example.com")
                .build();
    }

    @Test
    void saveUser_Normal_ReturnUser() {
        given(userRepository.findByEmail("user@example.com")).willReturn(Optional.empty());
        given(userRepository.save(user)).willReturn(user);

        User added = userService.saveUser(user);

        Assertions.assertThat(added)
                .isNotNull()
                .usingRecursiveComparison().isEqualTo(user);

        verify(userRepository, times(1)).findByEmail("user@example.com");
        verify(userRepository, times(1)).save(user);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void saveUser_WhenEmailExists_Exception() {
        given(userRepository.findByEmail("user@example.com")).willReturn(Optional.of(user));

        Throwable thrown = Assertions.catchException(() -> userService.saveUser(user));

        Assertions.assertThat(thrown)
                .isInstanceOf(UserAlreadyExistException.class)
                .hasMessage("Пользователь уже существует.");

        verify(userRepository, times(1)).findByEmail("user@example.com");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUser_NameAndEmail_Normal() {
        final User updatedData = User.builder()
                .id(1L)
                .name("updatedName")
                .email("updatedName@example.com")
                .build();

        given(userRepository.canUpdate(1L, "updatedName@example.com")).willReturn(true);
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(userRepository.update(user)).willReturn(user);

        User updatedUser = userService.updateUser(updatedData);

        Assertions.assertThat(updatedUser.getEmail()).isEqualTo("updatedName@example.com");
        Assertions.assertThat(updatedUser.getName()).isEqualTo("updatedName");

        verify(userRepository, times(1)).canUpdate(1L, "updatedName@example.com");
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).update(user);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUser_EmailIsExists_Exception() {
        final User updatedData = User.builder()
                .id(2L)
                .name("user2")
                .email("user@example.com")
                .build();
        given(userRepository.canUpdate(2L, "user@example.com")).willReturn(false);

        Throwable thrown = Assertions.catchException(() -> userService.updateUser(updatedData));

        Assertions.assertThat(thrown)
                .isInstanceOf(UserAlreadyExistException.class)
                .hasMessage("Пользователь с таким email существует.");

        verify(userRepository, times(1)).canUpdate(2L, "user@example.com");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUser_OnlyName_Normal() {
        User updatedData = User.builder()
                .id(1L)
                .name("updatedName")
                .build();

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(userRepository.update(user)).willReturn(user);

        User updatedUser = userService.updateUser(updatedData);

        Assertions.assertThat(updatedUser.getEmail()).isEqualTo("user@example.com");
        Assertions.assertThat(updatedUser.getName()).isEqualTo("updatedName");

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).update(user);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUser_OnlyEmail_Normal() {
        User updatedData = User.builder()
                .id(1L)
                .email("updatedName@example.com")
                .build();

        given(userRepository.canUpdate(1L, "updatedName@example.com")).willReturn(true);
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(userRepository.update(user)).willReturn(user);

        User updatedUser = userService.updateUser(updatedData);

        Assertions.assertThat(updatedUser.getEmail()).isEqualTo("updatedName@example.com");
        Assertions.assertThat(updatedUser.getName()).isEqualTo("user");

        verify(userRepository, times(1)).canUpdate(1L, "updatedName@example.com");
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).update(user);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUser_EmailIfDuplicate_Normal() {
        User updatedData = User.builder()
                .id(1L)
                .email("user@example.com")
                .build();

        given(userRepository.canUpdate(1L, "user@example.com")).willReturn(true);
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(userRepository.update(user)).willReturn(user);

        User returnedUser = userService.updateUser(updatedData);

        Assertions.assertThat(returnedUser.getEmail()).isEqualTo("user@example.com");
        Assertions.assertThat(returnedUser.getName()).isEqualTo("user");

        verify(userRepository, times(1)).canUpdate(1L, "user@example.com");
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).update(user);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void findUserById_Normal_ReturnUser() {
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        User actual = userService.findUserById(1L);

        Assertions.assertThat(actual)
                .isNotNull()
                .usingRecursiveComparison().isEqualTo(user);
        verify(userRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void findUserById_WrongId_NotFoundException() {
        given(userRepository.findById(999L)).willReturn(Optional.empty());

        Throwable thrown = Assertions.catchException(() -> userService.findUserById(999L));

        Assertions.assertThat(thrown)
                .isInstanceOf(NotFoundException.class)
                .hasMessage(String.format("Пользователь c id=%d не найден", 999L));

        verify(userRepository, times(1)).findById(999L);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deleteUser_Normal() {
        willDoNothing().given(userRepository).delete(anyLong());

        userService.deleteUser(anyLong());

        verify(userRepository, times(1)).delete(anyLong());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void findAllUsers_Normal_ReturnTwoUsers() {
        User user2 = User.builder()
                .id(2L)
                .name("user2")
                .email("user2@example.com")
                .build();
        given(userRepository.findAll()).willReturn(List.of(user, user2));

        List<User> users = userService.findAllUsers();

        Assertions.assertThat(users)
                .isNotNull()
                .hasSize(2)
                .contains(user, user2);
        verify(userRepository, times(1)).findAll();
        verifyNoMoreInteractions(userRepository);
    }
}