package ru.practicum.shareit.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UserAlreadyExistException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

@SpringBootTest
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("user")
                .email("user@example.com")
                .build();
        userDto = UserDto.builder()
                .id(1L)
                .name("user")
                .email("user@example.com")
                .build();
    }

    @Test
    void saveUser_Normal_ReturnUser() {
        BDDMockito.given(userRepository.save(user)).willReturn(user);

        UserDto added = userService.saveUser(userDto);

        Assertions.assertThat(added)
                .isNotNull()
                .usingRecursiveComparison().isEqualTo(user);

        Mockito.verify(userRepository, Mockito.times(1)).save(user);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void saveUser_WhenEmailExists_Exception() {
        //BDDMockito.given(userRepository.findByEmail("user@example.com")).willReturn(Optional.of(user));

        BDDMockito.given(userRepository.save(user)).willThrow(UserAlreadyExistException.class);

        Throwable thrown = Assertions.catchException(() -> userService.saveUser(userDto));

        Assertions.assertThat(thrown)
                .isInstanceOf(UserAlreadyExistException.class);

        Mockito.verify(userRepository, Mockito.times(1)).save(user);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUser_NameAndEmail_Normal() {
        final UserDto updatedData = UserDto.builder()
                .id(1L)
                .name("updatedName")
                .email("updatedName@example.com")
                .build();

        BDDMockito.given(userRepository.canNotUpdate(1L, "updatedName@example.com")).willReturn(false);
        BDDMockito.given(userRepository.findById(1L)).willReturn(Optional.of(user));
        BDDMockito.given(userRepository.save(user)).willReturn(user);

        UserDto updatedUser = userService.updateUser(updatedData);

        Assertions.assertThat(updatedUser.getEmail()).isEqualTo("updatedName@example.com");
        Assertions.assertThat(updatedUser.getName()).isEqualTo("updatedName");

        Mockito.verify(userRepository, Mockito.times(1)).canNotUpdate(1L, "updatedName@example.com");
        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(userRepository, Mockito.times(1)).save(user);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUser_EmailIsExists_Exception() {
        final UserDto updatedData = UserDto.builder()
                .id(2L)
                .name("user2")
                .email("user@example.com")
                .build();
        BDDMockito.given(userRepository.canNotUpdate(2L, "user@example.com")).willReturn(true);

        Throwable thrown = Assertions.catchException(() -> userService.updateUser(updatedData));

        Assertions.assertThat(thrown)
                .isInstanceOf(UserAlreadyExistException.class)
                .hasMessage("Пользователь с таким email существует.");

        Mockito.verify(userRepository, Mockito.times(1)).canNotUpdate(2L, "user@example.com");
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUser_OnlyName_Normal() {
        UserDto updatedData = UserDto.builder()
                .id(1L)
                .name("updatedName")
                .build();

        BDDMockito.given(userRepository.findById(1L)).willReturn(Optional.of(user));
        BDDMockito.given(userRepository.save(user)).willReturn(user);

        UserDto updatedUser = userService.updateUser(updatedData);

        Assertions.assertThat(updatedUser.getEmail()).isEqualTo("user@example.com");
        Assertions.assertThat(updatedUser.getName()).isEqualTo("updatedName");

        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(userRepository, Mockito.times(1)).save(user);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUser_OnlyEmail_Normal() {
        UserDto updatedData = UserDto.builder()
                .id(1L)
                .email("updatedName@example.com")
                .build();

        BDDMockito.given(userRepository.canNotUpdate(1L, "updatedName@example.com")).willReturn(false);
        BDDMockito.given(userRepository.findById(1L)).willReturn(Optional.of(user));
        BDDMockito.given(userRepository.save(user)).willReturn(user);

        UserDto updatedUser = userService.updateUser(updatedData);

        Assertions.assertThat(updatedUser.getEmail()).isEqualTo("updatedName@example.com");
        Assertions.assertThat(updatedUser.getName()).isEqualTo("user");

        Mockito.verify(userRepository, Mockito.times(1)).canNotUpdate(1L, "updatedName@example.com");
        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(userRepository, Mockito.times(1)).save(user);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUser_EmailIfDuplicate_Normal() {
        UserDto updatedData = UserDto.builder()
                .id(1L)
                .email("user@example.com")
                .build();

        BDDMockito.given(userRepository.canNotUpdate(1L, "user@example.com")).willReturn(false);
        BDDMockito.given(userRepository.findById(1L)).willReturn(Optional.of(user));
        BDDMockito.given(userRepository.save(user)).willReturn(user);

        UserDto returnedUser = userService.updateUser(updatedData);

        Assertions.assertThat(returnedUser.getEmail()).isEqualTo("user@example.com");
        Assertions.assertThat(returnedUser.getName()).isEqualTo("user");

        Mockito.verify(userRepository, Mockito.times(1)).canNotUpdate(1L, "user@example.com");
        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(userRepository, Mockito.times(1)).save(user);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void findUserById_Normal_ReturnUser() {
        BDDMockito.given(userRepository.findById(1L)).willReturn(Optional.of(user));

        UserDto actual = userService.findUserById(1L);

        Assertions.assertThat(actual)
                .isNotNull()
                .usingRecursiveComparison().isEqualTo(user);
        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void findUserById_WrongId_NotFoundException() {
        BDDMockito.given(userRepository.findById(999L)).willReturn(Optional.empty());

        Throwable thrown = Assertions.catchException(() -> userService.findUserById(999L));

        Assertions.assertThat(thrown)
                .isInstanceOf(NotFoundException.class)
                .hasMessage(String.format("Пользователь c id=%d не найден", 999L));

        Mockito.verify(userRepository, Mockito.times(1)).findById(999L);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deleteUser_Normal() {
        BDDMockito.willDoNothing().given(userRepository).deleteById(ArgumentMatchers.anyLong());

        userService.deleteUser(ArgumentMatchers.anyLong());

        Mockito.verify(userRepository, Mockito.times(1)).deleteById(ArgumentMatchers.anyLong());
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void findAllUsers_Normal_ReturnTwoUsers() {
        User user2 = User.builder()
                .id(2L)
                .name("user2")
                .email("user2@example.com")
                .build();
        BDDMockito.given(userRepository.findAll()).willReturn(List.of(user, user2));

        List<UserDto> users = userService.findAllUsers();

        Assertions.assertThat(users)
                .isNotNull()
                .hasSize(2)
                .contains(UserMapper.userToDto(user),
                        UserMapper.userToDto(user2));
        Mockito.verify(userRepository, Mockito.times(1)).findAll();
        Mockito.verifyNoMoreInteractions(userRepository);
    }
}