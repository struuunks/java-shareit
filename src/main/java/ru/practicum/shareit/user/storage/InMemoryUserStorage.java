package ru.practicum.shareit.user.storage;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.InvalidException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InMemoryUserStorage implements UserStorage {

    static final String EMAIL_PATTERN = "\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*\\.\\w{2,4}";
    Long id = 0L;
    final Map<Long, User> users = new HashMap<>();

    public Long generateId() {
        return ++id;
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public User getUserById(Long userId) {
        if (users.get(userId) == null) {
            throw new IdNotFoundException("Пользователь с айди " + userId + " не найден");
        } else {
            return users.get(userId);
        }
    }

    @Override
    public User createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        emailValidator(user.getId(), user.getEmail());
        user.setId(generateId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(Long userId, UserDto userDto) {
        User user = users.get(userId);
        if (!users.containsKey(userId)) {
            throw new IdNotFoundException("Пользователь с айди " + userId + " не найден");
        } else {
            if (userDto.getName() != null) {
                user.setName(userDto.getName());
            }
            if (userDto.getEmail() != null) {
                checkEmail(userDto.getEmail(), userId);
                user.setEmail(userDto.getEmail());
            }
            users.put(userId, user);
        }
        return user;
    }

    @Override
    public void deleteUser(Long userId) {
        if (!users.containsKey(userId)) {
            throw new IdNotFoundException("Пользователь с айди " + userId + " не найден");
        } else {
            users.remove(userId);
        }
    }

    private void emailValidator(Long userId, String email) {
        if (email == null) {
            throw new InvalidException("Отсутствует электронная почта");
        }
        if (!patternMatches(email)) {
            throw new InvalidException("Недействительная электронная почта");
        }
        checkEmail(email, userId);
    }

    private void checkEmail(String email, Long userId) {
        Map<Long, String> emails = users.entrySet()
                .stream()
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey, entry -> entry.getValue().getEmail()
                        )
                );
        if (emails.containsValue(email) && !emails.get(userId).equals(email)) {
            throw new ValidationException("Пользователь с данной электронной почтой уже зарегестрирован");
        }
    }

    private boolean patternMatches(String emailAddress) {
        return Pattern.compile(EMAIL_PATTERN)
                .matcher(emailAddress)
                .matches();
    }
}
