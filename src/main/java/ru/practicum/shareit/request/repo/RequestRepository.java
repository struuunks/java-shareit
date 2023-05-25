package ru.practicum.shareit.request.repo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequestorIdOrderByCreatedAsc(Long requestorId);

    List<ItemRequest> findItemRequestsByRequestorIdNotOrderByCreatedAsc(Long userId, Pageable pageable);
}
