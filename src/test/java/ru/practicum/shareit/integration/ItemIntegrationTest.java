package ru.practicum.shareit.integration;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentNewDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class ItemIntegrationTest {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    private final ItemService itemService;

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
        Item itemAdded = itemRepository.save(item);

        LocalDateTime now = LocalDateTime.now();
        Booking booking = Booking.builder()
                .item(itemAdded).booker(added2).status(BookingStatus.APPROVED)
                .start(now.minusDays(5))
                .end(now.minusDays(4))
                .build();
        bookingRepository.save(booking);


        CommentNewDto comment = CommentNewDto.builder()
                .text("отличный молоток")
                .build();

        CommentDto commentDto = itemService.addComment(added2.getId(), itemAdded.getId(), comment);

        Assertions.assertThat(commentDto.getAuthorName()).isEqualTo("tester");
        Assertions.assertThat(commentDto.getId()).isEqualTo(1L);
        Assertions.assertThat(commentDto.getText()).isEqualTo(comment.getText());
    }
}
