package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.user.User;

import java.util.List;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRepositoryTest {

    private final TestEntityManager tem;
    private final ItemRepository itemRepository;

    @Test
    void test_search_findTwoItems() {

        User user = User.builder().name("user").email("user@example.com").build();
        tem.persist(user);
        Item item1 = Item.builder().name("стул").description("отличный")
                .owner(user).available(true).build();
        tem.persist(item1);
        Item item2 = Item.builder().name("табурет").description("лучше стула")
                .owner(user).available(true).build();
        tem.persist(item2);
        Item item3 = Item.builder().name("табурет").description("мягкий")
                .owner(user).available(false).build();
        tem.persist(item3);

        PageRequest pageRequest = PageRequest.of(0, 20);

        List<Item> result1 = itemRepository.search("стул", pageRequest);
        Assertions.assertThat(result1).isNotNull().hasSize(2);
        Assertions.assertThat(result1)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(List.of(item1, item2));
    }

    @Test
    void test_search_findOneItem() {

        User user = User.builder().name("user").email("user@example.com").build();
        tem.persist(user);
        Item item1 = Item.builder().name("стул").description("отличный")
                .owner(user).available(true).build();
        tem.persist(item1);
        Item item2 = Item.builder().name("табурет").description("лучше стула")
                .owner(user).available(true).build();
        tem.persist(item2);
        Item item3 = Item.builder().name("табурет").description("мягкий")
                .owner(user).available(false).build();
        tem.persist(item3);

        PageRequest pageRequest = PageRequest.of(0, 20);

        // находит табурет с available true
        List<Item> result2 = itemRepository.search("табурет", pageRequest);
        Assertions.assertThat(result2).isNotNull().hasSize(1);
        Assertions.assertThat(result2)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(List.of(item2));
    }

    @Test
    void test_search_NoFoundItems() {
        User user = User.builder().name("user").email("user@example.com").build();
        tem.persist(user);
        Item item1 = Item.builder().name("стул").description("отличный")
                .owner(user).available(true).build();
        tem.persist(item1);
        Item item2 = Item.builder().name("табурет").description("лучше стула")
                .owner(user).available(true).build();
        tem.persist(item2);
        Item item3 = Item.builder().name("дрель").description("быстрая")
                .owner(user).available(false).build();
        tem.persist(item3);

        PageRequest pageRequest = PageRequest.of(0, 20);

        List<Item> result2 = itemRepository.search("дрель", pageRequest);
        Assertions.assertThat(result2).isNotNull().hasSize(0);
    }

    @Test
    void test_search_EmptySearchText() {
        User user = User.builder().name("user").email("user@example.com").build();
        tem.persist(user);
        Item item1 = Item.builder().name("стул").description("отличный")
                .owner(user).available(true).build();
        tem.persist(item1);
        Item item2 = Item.builder().name("табурет").description("лучше стула")
                .owner(user).available(true).build();
        tem.persist(item2);
        Item item3 = Item.builder().name("стол").description("мягкий")
                .owner(user).available(false).build();
        tem.persist(item3);

        PageRequest pageRequest = PageRequest.of(0, 20);

        List<Item> result2 = itemRepository.search("", pageRequest);
        Assertions.assertThat(result2).isNotNull().hasSize(0);
    }
}