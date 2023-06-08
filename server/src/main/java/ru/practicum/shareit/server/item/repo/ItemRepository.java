package ru.practicum.shareit.server.item.repo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.server.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(" select i from Item i " +
            "where upper(i.name) like upper(concat('%', ?1, '%')) " +
            "or upper(i.description) like upper(concat('%', ?1, '%')) " +
            "and i.available = true")
    List<Item> search(String text, Pageable pageable);

    List<Item> findByOwnerIdOrderByIdAsc(Long userId, Pageable pageable);

    List<Item> findItemsByRequestId(Long requestId);
}
