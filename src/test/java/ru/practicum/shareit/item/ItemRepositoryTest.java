package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repo.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Test
    public void testSearch() {
        User owner = new User(1L, "Owner", "owner@yandex.ru");
        owner = userRepository.save(owner);

        User requestor = new User(2L, "Requestor", "requestor@yandex.ru");
        requestor = userRepository.save(requestor);

        ItemRequest itemRequest1 =
                new ItemRequest(null, "item 1 description", requestor, LocalDateTime.now());
        itemRequest1 = requestRepository.save(itemRequest1);

        Item item1 =
                new Item(null, "item 1", "item 1 description", true, owner, itemRequest1);
        item1 = itemRepository.save(item1);

        ItemRequest itemRequest2 =
                new ItemRequest(null, "item 2 description", requestor, LocalDateTime.now());
        itemRequest2 = requestRepository.save(itemRequest2);

        Item item2 =
                new Item(null, "item 2", "item 2 description", true, owner, itemRequest2);
        item2 = itemRepository.save(item2);

        ItemRequest itemRequest3 =
                new ItemRequest(null, "item 3 description", requestor, LocalDateTime.now());
        itemRequest3 = requestRepository.save(itemRequest3);

        Item item3 =
                new Item(null, "item 3", "item 3 description", true, owner, itemRequest1);
        item3 = itemRepository.save(item3);

        List<Item> foundItems = itemRepository.findAll();
        assertEquals(3, foundItems.size());

        assertEquals(foundItems.get(0).getId(), item1.getId());
        assertEquals(foundItems.get(0).getName(), item1.getName());
        assertEquals(foundItems.get(0).getDescription(), item1.getDescription());
        assertEquals(foundItems.get(0).getOwner().getId(), item1.getOwner().getId());

        assertEquals(foundItems.get(2).getId(), item3.getId());
        assertEquals(foundItems.get(2).getName(), item3.getName());
        assertEquals(foundItems.get(2).getDescription(), item3.getDescription());
        assertEquals(foundItems.get(2).getOwner().getId(), item3.getOwner().getId());
    }
}
