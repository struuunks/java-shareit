package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoReturned;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.booking.model.Status.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingServiceImpl bookingService;

    private final User user = new User(1L, "User", "user@yandex.ru");
    private final User user2 = new User(2L, "User2", "user2@yandex.ru");
    private final Item item = new Item(1L,"item", "description", true, user, null);
    private final Booking booking = new Booking(
            1L,
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().plusDays(1),
            item,
            user2,
            WAITING
    );
    private final BookingDtoReturned bookingDtoReturned = BookingMapper.toBookingDtoReturned(booking);

    @Test
    void createBookingTest() throws Exception {
        when(bookingService.createBooking(any(), anyLong())).thenReturn(bookingDtoReturned);

        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDtoReturned))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(booking.getItem().getName()), String.class))
                .andExpect(jsonPath("$.booker.name", is(user2.getName()), String.class));
    }

    @Test
    void bookingConfirmationTest() throws Exception {
        bookingDtoReturned.setStatus(APPROVED);
        when(bookingService.bookingConfirmation(anyLong(), anyBoolean(), anyLong())).thenReturn(bookingDtoReturned);

        mvc.perform(patch("/bookings/{id}", booking.getId())
                        .param("approved", "true")
                        .content(objectMapper.writeValueAsString(bookingDtoReturned))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(APPROVED.toString()), String.class));
    }

    @Test
    void getBookingByIdTest() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(bookingDtoReturned);
        mvc.perform(get("/bookings/{id}", booking.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(booking.getItem().getName()), String.class));
    }

    @Test
    void getAllBookingsByUserTest() throws Exception {
        when(bookingService.getAllBookingsByUser(anyString(), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDtoReturned));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", user2.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(booking.getItem().getName()), String.class));

        Mockito.verify(bookingService, Mockito.times(1))
                .getAllBookingsByUser(anyString(), anyLong(), anyInt(), anyInt());
    }

    @Test
    void getAllBookingsByOwnerTest() throws Exception {
        when(bookingService.getAllBookingsByOwner(anyString(), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDtoReturned));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(booking.getItem().getName()), String.class));

        Mockito.verify(bookingService, Mockito.times(1))
                .getAllBookingsByOwner(anyString(), anyLong(), anyInt(), anyInt());
    }
}