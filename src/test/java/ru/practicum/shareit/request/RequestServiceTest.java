package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.InvalidException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class RequestServiceTest {
    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private RequestService requestService;

    private final User user = new User(null, "user", "user@yandex.ru");
    private final User user2 = new User(null, "user2", "user2@yandex.ru");
    private final ItemRequest request = new ItemRequest(null, "request", user2, LocalDateTime.now());

    @Test
    void createRequestTest() {
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        user.setId(userDto.getId());

        ItemRequestDto requestDto =
                requestService.createRequest(RequestMapper.toItemRequestDto(request), user.getId());
        request.setId(requestDto.getId());

        assertEquals(requestDto.getId(), request.getId());
        assertEquals(requestDto.getDescription(), request.getDescription());
        assertEquals(requestDto.getRequestorId(), user.getId());
    }

    @Test
    void createRequestWrongUserTest() {
        assertThrows(DataNotFoundException.class,
                () -> requestService.createRequest(RequestMapper.toItemRequestDto(request), 5L));
    }

    @Test
    void createRequestEmptyTextTest() {
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        user.setId(userDto.getId());

        request.setDescription("");

        assertThrows(InvalidException.class,
                () -> requestService.createRequest(RequestMapper.toItemRequestDto(request), user.getId()));
    }

    @Test
    void getWrongUserRequestsTest() {
        assertThrows(DataNotFoundException.class,
                () -> requestService.getUserRequests(5L));
    }

    @Test
    void getUserRequestsTest() {
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        user.setId(userDto.getId());

        ItemRequestDto requestDto =
                requestService.createRequest(RequestMapper.toItemRequestDto(request), user.getId());
        request.setId(requestDto.getId());

        ItemRequest request1 = new ItemRequest(null, "request1", user2, LocalDateTime.now());
        requestService.createRequest(RequestMapper.toItemRequestDto(request1), user.getId());

        ItemRequest request2 = new ItemRequest(null, "request2", user2, LocalDateTime.now());
        requestService.createRequest(RequestMapper.toItemRequestDto(request2), user.getId());

        List<ItemRequestDto> requests = requestService.getUserRequests(user.getId());

        assertEquals(requests.size(), 3);
        assertEquals(requests.get(0).getDescription(), request.getDescription());
        assertEquals(requests.get(1).getDescription(), request1.getDescription());
        assertEquals(requests.get(2).getDescription(), request2.getDescription());
    }

    @Test
    void getAllRequestsWrongUserTest() {
        assertThrows(DataNotFoundException.class,
                () -> requestService.getAllRequests(5L, 0, 10));
    }

    @Test
    void getAllRequestsTest() {
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        UserDto userDto1 = userService.createUser(UserMapper.toUserDto(user2));
        user2.setId(userDto1.getId());
        ItemRequestDto requestDto =
                requestService.createRequest(RequestMapper.toItemRequestDto(request), user.getId());
        request.setId(requestDto.getId());
        ItemRequest request1 = new ItemRequest(null, "request1", user2, LocalDateTime.now());
        requestService.createRequest(RequestMapper.toItemRequestDto(request1), user2.getId());
        ItemRequest request2 = new ItemRequest(null, "request2", user2, LocalDateTime.now());
        requestService.createRequest(RequestMapper.toItemRequestDto(request2), user.getId());
        List<ItemRequestDto> requests = requestService.getAllRequests(user2.getId(), 0, 10);

        assertEquals(requests.size(), 2);
        assertEquals(requests.get(0).getDescription(), request.getDescription());
        assertEquals(requests.get(1).getDescription(), request2.getDescription());
    }

    @Test
    void getRequestByIdWrongUserTest() {
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        user.setId(userDto.getId());

        ItemRequestDto requestDto =
                requestService.createRequest(RequestMapper.toItemRequestDto(request), user.getId());
        request.setId(requestDto.getId());

        assertThrows(DataNotFoundException.class,
                () -> requestService.getRequestById(5L, request.getId()));
    }

    @Test
    void getRequestByWrongIdTest() {
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        user.setId(userDto.getId());

        assertThrows(DataNotFoundException.class,
                () -> requestService.getRequestById(user.getId(), 5L));
    }

    @Test
    void getRequestsByIdTest() {
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        ItemRequestDto requestDto =
                requestService.createRequest(RequestMapper.toItemRequestDto(request), user.getId());
        request.setId(requestDto.getId());
        requestDto = requestService.getRequestById(user.getId(), request.getId());

        assertEquals(requestDto.getId(), request.getId());
        assertEquals(requestDto.getDescription(), request.getDescription());
        assertEquals(requestDto.getRequestorId(), user.getId());
    }
}
