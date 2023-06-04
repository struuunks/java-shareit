package ru.practicum.shareit.server.user.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.server.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
