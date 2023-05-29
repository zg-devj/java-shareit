package ru.practicum.shareit.integration;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentNewDto;
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
public class IntegrationTest {

    private final EntityManager tem;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestService itemRequestService;
    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

//    private final JdbcTemplate jdbcTemplate;
//
//    @AfterEach
//    void tearDown() {
//        JdbcTestUtils.deleteFromTables(jdbcTemplate,
//                "requests", "comments",
//                "bookings", "items", "users");
//    }

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
                .requestor(user)
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

        assertThat(result.getRequestor(), is(user));
        assertThat(result.getItems(), nullValue());
    }

    @Test
    void addComment_Normal() {
        User user1 = User.builder()
                .name("admin").email("admin@example.com")
                .build();
        User user2 = User.builder()
                .name("tester").email("tester@example.com")
                .build();
        User added1 = userRepository.save(user1);
        User added2 = userRepository.save(user2);

        Item item = Item.builder()
                .name("молоток").description("стальной молоток").available(true)
                .owner(added1)
                .build();
        Item itemAdded =  itemRepository.save(item);

        LocalDateTime now = LocalDateTime.now();
        Booking booking = Booking.builder()
                .item(itemAdded).booker(added2).status(BookingStatus.APPROVED)
                .start(now.minusDays(5))
                .end(now.minusDays(4))
                .build();
        tem.persist(booking);
        tem.flush();


        CommentNewDto comment = CommentNewDto.builder()
                .text("отличный молоток")
                .build();

        CommentDto commentDto = itemService.addComment(added2.getId(), itemAdded.getId(), comment);

        Assertions.assertThat(commentDto.getAuthorName()).isEqualTo("tester");
        Assertions.assertThat(commentDto.getId()).isEqualTo(1L);
        Assertions.assertThat(commentDto.getText()).isEqualTo(comment.getText());

    }
}
