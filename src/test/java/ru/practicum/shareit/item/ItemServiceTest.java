package ru.practicum.shareit.item;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UserAlreadyExistException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User user;
    private Item item1;
    private Item item2;

    private User user2;
    private Item item3;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L).name("user").email("user@example.com")
                .build();
        item1 = Item.builder()
                .id(1L)
                .name("hammer").description("good steel hammer").available(true)
                .owner(user)
                .build();
        item2 = Item.builder()
                .id(2L)
                .name("drill").description("best drill").available(false)
                .build();

        user2 = User.builder()
                .id(2L).name("user2").email("user2@example.com")
                .build();

        item3 = Item.builder()
                .id(2L)
                .name("trap").description("long best trap").available(true)
                .owner(user2)
                .build();
    }

    @Test
    void saveItem_Normal() {
        given(userService.findUserById(1L)).willReturn(user);
        given(itemRepository.save(item2)).willReturn(item2);

        Item added = itemService.saveItem(1L, item2);

        Assertions.assertThat(added)
                .isNotNull()
                .usingRecursiveComparison().isEqualTo(item2);
        Assertions.assertThat(added.getOwner()).isEqualTo(user);

        verify(userService, times(1)).findUserById(1L);
        verifyNoMoreInteractions(userService);
        verify(itemRepository, times(1)).save(item2);
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void saveItem_NotExistUser_ReturnNotFoundException() {
        String msg = String.format("Пользователь c id=%d не найден", 999L);

        given(userService.findUserById(999L)).willThrow(new NotFoundException(msg));

        Throwable thrown = Assertions.catchException(() -> itemService.saveItem(999L, item2));

        Assertions.assertThat(thrown)
                .isInstanceOf(NotFoundException.class)
                .hasMessage(msg);

        verify(userService, times(1)).findUserById(999L);
        verifyNoMoreInteractions(userService);
        verifyNoInteractions(itemRepository);
    }

    @Test
    void updateItem_Normal() {
        Item dataToUpdate = Item.builder()
                .id(1L)
                .name("hammer updated")
                .description("good steel hammer updated")
                .available(false)
                .build();

        given(userService.findUserById(1L)).willReturn(user);
        given(itemRepository.findById(1L)).willReturn(Optional.of(item1));
        given(itemRepository.update(item1)).willReturn(item1);

        Item updated = itemService.updateItem(1L, dataToUpdate);

        Assertions.assertThat(updated.getName()).isEqualTo("hammer updated");
        Assertions.assertThat(updated.getDescription()).isEqualTo("good steel hammer updated");
        Assertions.assertThat(updated.getAvailable()).isEqualTo(false);

        verify(userService, times(1)).findUserById(1L);
        verifyNoMoreInteractions(userService);
        verify(itemRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).update(item1);
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void updateItem_UserNotFound_ReturnNotFoundException() {
        Item dataToUpdate = Item.builder()
                .id(1L)
                .name("hammer updated")
                .description("good steel hammer updated")
                .available(false)
                .build();

        String msg = String.format("Пользователь c id=%d не найден", 999L);
        given(userService.findUserById(999L)).willThrow(new NotFoundException(msg));

        Throwable thrown = Assertions.catchException(() -> itemService.updateItem(999L, dataToUpdate));

        Assertions.assertThat(thrown)
                .isInstanceOf(NotFoundException.class)
                .hasMessage(msg);


        verify(userService, times(1)).findUserById(999L);
        verifyNoMoreInteractions(userService);
        verifyNoInteractions(itemRepository);
    }

    @Test
    void updateItem_ItemNotFound_ReturnNotFoundException() {
        Item dataToUpdate = Item.builder()
                .id(999L)
                .name("hammer updated")
                .description("good steel hammer updated")
                .available(false)
                .build();

        given(userService.findUserById(1L)).willReturn(user);
        given(itemRepository.findById(999L)).willReturn(Optional.empty());

        Throwable thrown = Assertions.catchException(() -> itemService.updateItem(1L, dataToUpdate));

        Assertions.assertThat(thrown)
                .isInstanceOf(NotFoundException.class)
                .hasMessage(String.format("Вещь c id=%d не найдена", 999L));


        verify(userService, times(1)).findUserById(1L);
        verifyNoMoreInteractions(userService);
        verify(itemRepository, times(1)).findById(999L);
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void updateItem_ItemNotBelongsUser_ReturnForbiddenException() {
        Item dataToUpdate = Item.builder()
                .id(1L)
                .name("hammer")
                .description("good steel hammer")
                .available(false)
                .build();
        given(userService.findUserById(2L)).willReturn(user2);
        given(itemRepository.findById(1L)).willReturn(Optional.of(item1));

        Throwable thrown = Assertions.catchException(() -> itemService.updateItem(2L, dataToUpdate));

        Assertions.assertThat(thrown)
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("У вас нет прав для редактирования.");

        verify(userService, times(1)).findUserById(2L);
        verifyNoMoreInteractions(userService);
        verify(itemRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void findItemById_Normal() {
        given(itemRepository.findById(1L)).willReturn(Optional.of(item1));

        Item actual = itemService.findById(1L);

        Assertions.assertThat(actual)
                .isNotNull()
                .usingRecursiveComparison().isEqualTo(item1);
        verify(itemRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(itemRepository);
        verifyNoInteractions(userService);
    }

    @Test
    void findById_WrongId_ReturnNotFoundException() {
        given(itemRepository.findById(999L)).willReturn(Optional.empty());

        Throwable thrown = Assertions.catchException(() -> itemService.findById(999L));

        Assertions.assertThat(thrown)
                .isInstanceOf(NotFoundException.class)
                .hasMessage(String.format("Вещь c id=%d не найдена", 999L));

        verify(itemRepository, times(1)).findById(999L);
        verifyNoMoreInteractions(itemRepository);
        verifyNoInteractions(userService);
    }

    @Test
    void findAllItemsByUserId() {
        List<Item> items = List.of(item1, item2);
        given(itemRepository.findAllByUserId(1L)).willReturn(items);

        List<Item> result = itemService.findAllByUserId(1L);

        Assertions.assertThat(result)
                .hasSize(2)
                .contains(item1, item2);

        verify(itemRepository, times(1)).findAllByUserId(1L);
        verifyNoMoreInteractions(itemRepository);
        verifyNoInteractions(userService);
    }

    @Test
    void search_Normal() {
        List<Item> items = List.of(item2, item3);
        given(itemRepository.findByNameAndByDescription("best")).willReturn(items);

        List<Item> result = itemService.search("best");

        Assertions.assertThat(result)
                .hasSize(2)
                .contains(item2, item3);

        verify(itemRepository, times(1)).findByNameAndByDescription("best");
        verifyNoMoreInteractions(itemRepository);
        verifyNoInteractions(userService);
    }

    @Test
    void search_WithEmptyText_ReturnEmptyList() {
        List<Item> itemsEmpty = new ArrayList<>();
        given(itemRepository.findByNameAndByDescription("")).willReturn(itemsEmpty);

        List<Item> result = itemService.search("");

        Assertions.assertThat(result)
                .hasSize(0);

        verify(itemRepository, times(1)).findByNameAndByDescription("");
        verifyNoMoreInteractions(itemRepository);
        verifyNoInteractions(userService);
    }

}