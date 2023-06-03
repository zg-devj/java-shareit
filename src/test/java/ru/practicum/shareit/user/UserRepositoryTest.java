package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserRepositoryTest {
    private final UserRepository userRepository;
    private final TestEntityManager tem;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = User.builder().name("user").email("user@example.com").build();
        tem.persist(user1);
        user2 = User.builder().name("tester").email("tester@example.com").build();
        tem.persist(user2);
    }

    @Test
    void test_canUpdate_Correct() {
        // пользователь может быть обновлен
        boolean resultTrue = userRepository.canUpdate(user1.getId(), "user-user@example.com");

        Assertions.assertThat(resultTrue).isTrue();
    }

    @Test
    void test_canUpdate_Wrong() {
        // пользователь не может быть обновлен
        boolean resultFalse = userRepository.canUpdate(user1.getId(), "tester@example.com");

        Assertions.assertThat(resultFalse).isFalse();
    }
}