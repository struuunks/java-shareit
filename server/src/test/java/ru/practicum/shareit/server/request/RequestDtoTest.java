package ru.practicum.shareit.server.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.request.mapper.RequestMapper;
import ru.practicum.shareit.server.request.model.ItemRequest;
import ru.practicum.shareit.server.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class RequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> jsonDto;

    private final User user = new User(5L, "user", "user@yandex.ru");
    private ItemRequest itemRequest;
    private final LocalDateTime ldt = LocalDateTime.now();

    private static final String TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS";

    @BeforeEach
    void setUp() {
        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requestor(user)
                .created(ldt)
                .build();
    }

    @Test
    void requestDtoTest() throws Exception {
        ItemRequestDto itemRequestDto = RequestMapper.toItemRequestDto(itemRequest);
        JsonContent<ItemRequestDto> result = jsonDto.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathNumberValue("$.requestorId").isEqualTo(5);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_PATTERN);
        String format = ldt.format(formatter);

        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(format);
    }
}
