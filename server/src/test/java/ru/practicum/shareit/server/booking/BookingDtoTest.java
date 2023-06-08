package ru.practicum.shareit.server.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.dto.BookingDtoReceived;
import ru.practicum.shareit.server.booking.dto.BookingDtoReturned;
import ru.practicum.shareit.server.booking.mapper.BookingMapper;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.server.booking.model.Status;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingDto> jsonDto;
    @Autowired
    private JacksonTester<BookingDtoReturned> jsonDtoReturned;
    @Autowired
    private JacksonTester<BookingDtoReceived> jsonDtoReceived;

    private Booking booking;
    private final User user = new User(11L, "user", "user@yandex.ru");
    private final Item item = new Item(10L, "item", "description", true, user, null);
    private final User booker = new User(20L, "booker", "booker@yandex.ru");
    private static final String TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
    private final LocalDateTime start =
            LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern(TIME_PATTERN)));
    private final LocalDateTime end =
            LocalDateTime.parse(LocalDateTime.now().plusHours(1).format(DateTimeFormatter.ofPattern(TIME_PATTERN)));


    @BeforeEach
    void setUp() {
        booking = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build();
    }

    @Test
    void bookingDtoTest() throws Exception {
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);
        JsonContent<BookingDto> result = jsonDto.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(start.toString());
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(end.toString());
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(10);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(20);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(Status.APPROVED.toString());
    }

    @Test
    void bookingDtoReturnedTest() throws Exception {
        BookingDtoReturned bookingDto = BookingMapper.toBookingDtoReturned(booking);
        JsonContent<BookingDtoReturned> result = jsonDtoReturned.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(start.toString());
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(end.toString());
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(Status.APPROVED.toString());
    }

    @Test
    void bookingDtoReceivedTest() throws Exception {
        BookingDtoReceived bookingDto = BookingMapper.toBookingDtoReceived(booking);
        JsonContent<BookingDtoReceived> result = jsonDtoReceived.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(start.toString());
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(end.toString());
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(10);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(Status.APPROVED.toString());
    }
}
