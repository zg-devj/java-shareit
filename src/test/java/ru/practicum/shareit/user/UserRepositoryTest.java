package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserRepositoryTest {
    private final UserRepository userRepository;
    private final TestEntityManager tem;

    @Test
    void test() {
        User user1 = User.builder()
                .name("user")
                .email("user@example.com")
                .build();
        User user2 = User.builder()
                .name("tester")
                .email("tester@example.com")
                .build();
        tem.persist(user1);
        tem.persist(user2);
        tem.flush();

        boolean resultFalse = userRepository.canUpdate(1L,"tester@example.com");
        // пользователь не может быть обновлен
        Assertions.assertThat(resultFalse).isFalse();

        // пользователь может быть обновлен
        boolean resultTrue = userRepository.canUpdate(1L,"user-user@example.com");
        Assertions.assertThat(resultTrue).isTrue();
    }
}