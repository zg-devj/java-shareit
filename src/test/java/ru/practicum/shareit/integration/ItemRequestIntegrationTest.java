package ru.practicum.shareit.integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.utils.Utils;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class ItemRequestIntegrationTest {

    private final EntityManager tem;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestService itemRequestService;

    @Test
    void saveItemRequest() {
        User user = User.builder()
                .name("user")
                .email("user@example.com")
                .build();
        User userSaved = userRepository.save(user);

        ItemRequestDto requestDto = ItemRequestDto.builder()
                .description("electric drill")
                .build();
        ItemRequestDto saved = itemRequestService.saveItemRequest(userSaved.getId(), requestDto);

        String query = "select ir from ItemRequest as ir where ir.id=:id";
        ItemRequest result = tem.createQuery(query, ItemRequest.class)
                .setParameter("id", saved.getId())
                .getSingleResult();

        assertThat(result, notNullValue());
        assertThat(result.getRequestor().getName(), equalTo(user.getName()));
        assertThat(result.getRequestor().getEmail(), equalTo(user.getEmail()));
        assertThat(result.getDescription(), equalTo(requestDto.getDescription()));
        assertThat(result.getId(), equalTo(saved.getId()));
    }

    @Test
    void getItemRequest() {
        // given
        User user = User.builder()
                .name("user")
                .email("user@example.com")
                .build();
        User user1 = userRepository.save(user);
        LocalDateTime now = LocalDateTime.now();
        ItemRequest itemRequest = ItemRequest.builder()
                .description("steal hammer")
                .requestor(user1)
                .created(now)
                .build();
        ItemRequest itemRequest1 = itemRequestRepository.save(itemRequest);

        // when
        ItemRequestDto itemRequestDto = itemRequestService.getItemRequest(user1.getId(), itemRequest1.getId());

        String query = "select ir from ItemRequest as ir where ir.id=:id";
        ItemRequest result = tem.createQuery(query, ItemRequest.class)
                .setParameter("id", itemRequestDto.getId())
                .getSingleResult();

        //then
        assertThat(itemRequestDto.getCreated(), is(now.format(Utils.dtFormatter)));
        assertThat(itemRequestDto.getDescription(), is(itemRequest.getDescription()));

        assertThat(result.getRequestor(), is(user1));
        assertThat(result.getItems(), nullValue());
    }
}
