package ru.practicum.shareit.integration;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentNewDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class ItemIntegrationTest {

    private final EntityManager em;
    private final ItemService itemService;

    @Test
    void addComment_Normal() {
        LocalDateTime now = LocalDateTime.now();

        User user1 = User.builder()
                .name("admin").email("admin@example.com")
                .build();
        em.persist(user1);
        User user2 = User.builder()
                .name("tester").email("tester@example.com")
                .build();
        em.persist(user2);
        Item item = Item.builder()
                .name("молоток").description("стальной молоток").available(true)
                .owner(user1)
                .build();
        em.persist(item);
        Booking booking = Booking.builder()
                .item(item).booker(user2).status(BookingStatus.APPROVED)
                .start(now.minusDays(5))
                .end(now.minusDays(4))
                .build();
        em.persist(booking);


        CommentNewDto comment = CommentNewDto.builder()
                .text("отличный молоток")
                .build();

        CommentDto commentDto = itemService.addComment(user2.getId(), item.getId(), comment);

        Assertions.assertThat(commentDto.getAuthorName()).isEqualTo("tester");
        Assertions.assertThat(commentDto.getId()).isEqualTo(1L);
        Assertions.assertThat(commentDto.getText()).isEqualTo(comment.getText());
    }
}
