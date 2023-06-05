package ru.practicum.shareit.server.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.server.exception.DataNotFoundException;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.mapper.UserMapper;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.user.repo.UserRepository;
import ru.practicum.shareit.server.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceITest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getAllUsersTest() {
        User user1 = createUser(1L, "User1", "user1@yandex.ru");
        User user2 = createUser(2L, "User2", "user2@yandex.ru");

        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        when(userRepository.findAll()).thenReturn(users);

        List<UserDto> result = userService.getAllUsers();

        assertEquals(users.size(), result.size());

        assertEquals(user1.getId(), result.get(0).getId());
        assertEquals(user1.getName(), result.get(0).getName());
        assertEquals(user1.getEmail(), result.get(0).getEmail());

        assertEquals(user2.getId(), result.get(1).getId());
        assertEquals(user2.getName(), result.get(1).getName());
        assertEquals(user2.getEmail(), result.get(1).getEmail());

        verify(userRepository).findAll();
    }

    @Test
    void getUserByIdTest() {
        User user = createUser(1L, "User", "user@yandex.ru");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UserDto result = userService.getUserById(user.getId());

        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());

        verify(userRepository).findById(1L);
    }

    @Test
    void getUserByWrongIdTest() {
        User user = createUser(1L, "User", "user@yandex.ru");
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> userService.getUserById(user.getId()));

        verify(userRepository).findById(user.getId());
    }

    @Test
    void createUserTest() {
        User user = createUser(1L, "User", "user@yandex.ru");
        UserDto userDto = UserMapper.toUserDto(user);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.createUser(userDto);

        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUserTest() {
        User existingUser = createUser(1L, "User", "user@yandex.ru");
        when(userRepository.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(userCaptor.capture())).thenReturn(existingUser);

        UserDto updatedUserDto = createUserDto("User2", "user2@yandex.ru");
        UserDto result = userService.updateUser(existingUser.getId(), updatedUserDto);

        assertEquals(updatedUserDto.getName(), result.getName());
        assertEquals(updatedUserDto.getEmail(), result.getEmail());

        verify(userRepository).findById(existingUser.getId());
        verify(userRepository).save(any(User.class));

        User savedUser = userCaptor.getValue();
        assertEquals(1L, savedUser.getId());
        assertEquals(updatedUserDto.getName(), savedUser.getName());
        assertEquals(updatedUserDto.getEmail(), savedUser.getEmail());
    }

    @Test
    void updateWrongUserTest() {
        User user = createUser(1L, "User", "user@yandex.ru");
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        UserDto updatedUserDto = createUserDto("User2", "user2@yandex.ru");
        assertThrows(DataNotFoundException.class, () -> userService.updateUser(user.getId(), updatedUserDto));

        verify(userRepository).findById(user.getId());
    }

    @Test
    void deleteUserTest() {
        User user = createUser(1L, "User", "user@yandex.ru");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        userService.deleteUser(user.getId());

        verify(userRepository).deleteById(user.getId());
    }

    private User createUser(Long id, String name, String email) {
        return User.builder()
                .id(id)
                .name(name)
                .email(email)
                .build();
    }

    private UserDto createUserDto(String name, String email) {
        return UserDto.builder()
                .name(name)
                .email(email)
                .build();
    }
}
