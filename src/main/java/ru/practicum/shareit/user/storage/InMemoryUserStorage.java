package ru.practicum.shareit.user.storage;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.InvalidException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.*;
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
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(Long userId) {
        return users.get(userId);
    }

    @Override
    public User createUser(User user) {
        emailValidator(user.getId(), user.getEmail());
        user.setId(generateId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(Long userId, User user) {
        if (user.getName() != null) {
            users.get(userId).setName(user.getName());
        }
        if (user.getEmail() != null) {
            emailValidator(userId, user.getEmail());
            users.get(userId).setEmail(user.getEmail());
        }
        return users.get(userId);
    }

    @Override
    public void deleteUser(Long userId) {
        users.remove(userId);
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
