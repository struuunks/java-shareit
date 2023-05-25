package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    void createUserTest() {
        User user = new User(1L, "user", "user@yandex.ru");
        User savedUser = userRepository.save(user);

        assertNotNull(savedUser.getId());
        assertEquals(user.getName(), savedUser.getName());
        assertEquals(user.getEmail(), savedUser.getEmail());
    }
}
