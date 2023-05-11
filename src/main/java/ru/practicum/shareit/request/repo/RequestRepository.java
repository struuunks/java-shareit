package ru.practicum.shareit.request.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {
}
