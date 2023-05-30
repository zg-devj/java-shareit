package ru.practicum.shareit.integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
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

    private final EntityManager em;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestService itemRequestService;
    private final ItemService itemService;
    private final ItemRepository itemRepository;


    @Test
    void saveItemRequest() {
        User user = User.builder()
                .name("user")
                .email("user@example.com")
                .build();
        em.persist(user);

        ItemRequestDto requestDto = ItemRequestDto.builder()
                .description("electric drill")
                .build();

        ItemRequestDto saved = itemRequestService.saveItemRequest(user.getId(), requestDto);


        String query = "select ir from ItemRequest as ir where ir.id=:id";

        ItemRequest result = em.createQuery(query, ItemRequest.class)
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
        LocalDateTime now = LocalDateTime.now();

        User user = User.builder()
                .name("user")
                .email("user@example.com")
                .build();
        em.persist(user);

        ItemRequest item = ItemRequest.builder()
                .description("steal hammer")
                .requestor(user)
                .created(now)
                .build();
        em.persist(item);

        // when
        ItemRequestDto itemRequestDto = itemRequestService.getItemRequest(user.getId(), item.getId());

        String query = "select ir from ItemRequest as ir where ir.id=:id";
        ItemRequest result = em.createQuery(query, ItemRequest.class)
                .setParameter("id", itemRequestDto.getId())
                .getSingleResult();

        //then
        assertThat(itemRequestDto.getCreated(), is(now));
        assertThat(itemRequestDto.getDescription(), is(item.getDescription()));
        assertThat(result.getRequestor(), is(user));
        assertThat(result.getItems(), nullValue());
    }
}
