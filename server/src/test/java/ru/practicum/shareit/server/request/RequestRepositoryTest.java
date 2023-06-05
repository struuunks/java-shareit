package ru.practicum.shareit.server.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.server.request.model.ItemRequest;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.user.repo.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class RequestRepositoryTest {
    @Autowired
    private ru.practicum.shareit.server.request.repo.RequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindItemRequestsByRequestorIdNotOrderByCreatedAsc() {
        User requestor = new User(1L, "requestor", "requestor@yandex.ru");
        requestor = userRepository.save(requestor);

        User user = new User(2L, "user", "user@yandex.ru");
        user = userRepository.save(user);

        ItemRequest request1 = new ItemRequest(1L, "first request", requestor,
                LocalDateTime.now().minusDays(1));
        request1 = requestRepository.save(request1);

        ItemRequest request2 = new ItemRequest(2L, "second request", user,
                LocalDateTime.now().minusHours(3));
        request2 = requestRepository.save(request2);

        ItemRequest request3 = new ItemRequest(3L, "third request", requestor, LocalDateTime.now());
        request3 = requestRepository.save(request3);

        List<ItemRequest> foundRequests = requestRepository
                .findItemRequestsByRequestorIdNotOrderByCreatedAsc(user.getId(), PageRequest.of(0, 10));

        assertEquals(2, foundRequests.size());

        assertEquals(foundRequests.get(0).getId(), request1.getId());
        assertEquals(foundRequests.get(0).getDescription(), request1.getDescription());
        assertEquals(foundRequests.get(0).getRequestor().getId(), request1.getRequestor().getId());

        assertEquals(foundRequests.get(1).getId(), request3.getId());
        assertEquals(foundRequests.get(1).getDescription(), request3.getDescription());
        assertEquals(foundRequests.get(1).getRequestor().getId(), request3.getRequestor().getId());
    }
}
