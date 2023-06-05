package ru.practicum.shareit.server.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.booking.dto.BookingDtoReturned;
import ru.practicum.shareit.server.booking.mapper.BookingMapper;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.server.booking.service.BookingService;
import ru.practicum.shareit.server.exception.DataNotFoundException;
import ru.practicum.shareit.server.exception.InvalidException;
import ru.practicum.shareit.server.item.dto.CommentDto;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.dto.ItemDtoOwner;
import ru.practicum.shareit.server.item.mapper.CommentMapper;
import ru.practicum.shareit.server.item.mapper.ItemMapper;
import ru.practicum.shareit.server.item.model.Comment;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.item.service.ItemServiceImpl;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.mapper.UserMapper;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class ItemServiceTest {
    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private BookingService bookingService;

    private final User user = new User(null, "user", "user@yandex.ru");
    private final Item item = new Item(null, "item", "description", true, user, null);

    @Test
    void createItemTest() {
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        ItemDto itemDto = itemService.createItem(ItemMapper.toItemDto(item), user.getId());

        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getOwner().getId(), itemDto.getOwner().getId());
    }

    @Test
    void createItemWrongUserTest() {
        assertThrows(DataNotFoundException.class,
                () -> itemService.createItem(ItemMapper.toItemDto(item), 5L));
    }

    @Test
    void createItemWrongRequestTest() {
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        user.setId(userDto.getId());

        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setRequestId(5L);

        assertThrows(DataNotFoundException.class, () -> itemService.createItem(itemDto, user.getId()));
    }

    @Test
    void createItemWithWrongUserTest() {
        assertThrows(DataNotFoundException.class, () -> itemService.createItem(ItemMapper.toItemDto(item), user.getId()));
    }

    @Test
    void updateItemWrongUserTest() {
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        ItemDto itemDto = itemService.createItem(ItemMapper.toItemDto(item), user.getId());
        item.setId(itemDto.getId());
        item.setName("new item name");

        assertThrows(DataNotFoundException.class,
                () -> itemService.updateItem(item.getId(), ItemMapper.toItemDto(item),  100L));
    }

    @Test
    void updateItemTest() {
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        ItemDto itemDto = itemService.createItem(ItemMapper.toItemDto(item), user.getId());
        item.setId(itemDto.getId());
        item.setName("new item name");
        ItemDto itemDtoUpdated = itemService.updateItem(item.getId(), ItemMapper.toItemDto(item), user.getId());

        assertEquals(item.getName(), itemDtoUpdated.getName());
        assertEquals(item.getDescription(), itemDtoUpdated.getDescription());
        assertEquals(item.getOwner().getId(), itemDtoUpdated.getOwner().getId());
    }

    @Test
    void updateItemWithWrongUserTest() {
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        ItemDto itemDto = itemService.createItem(ItemMapper.toItemDto(item), user.getId());
        item.setId(itemDto.getId());
        item.setName("new item name");

        assertThrows(DataNotFoundException.class, () -> itemService.updateItem(item.getId(), ItemMapper.toItemDto(item), 3L));
    }

    @Test
    void getItemByIdTest() {
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        ItemDto itemDto = itemService.createItem(ItemMapper.toItemDto(item), user.getId());
        item.setId(itemDto.getId());
        ItemDtoOwner itemDtoOwner = itemService.getItemById(item.getId(), user.getId());

        assertEquals(item.getName(), itemDtoOwner.getName());
        assertEquals(item.getDescription(), itemDtoOwner.getDescription());
    }

    @Test
    void getItemByWrongIdTest() {
        item.setId(5L);
        assertThrows(DataNotFoundException.class, () -> itemService.getItemById(item.getId(), user.getId()));
    }

    @Test
    void commentItemTest() throws InterruptedException {
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        user.setId(userDto.getId());

        ItemDto itemDto = itemService.createItem(ItemMapper.toItemDto(item), user.getId());
        item.setId(itemDto.getId());

        User author = new User(null, "author", "author@yandex.ru");
        UserDto authorDto = userService.createUser(UserMapper.toUserDto(author));
        author.setId(authorDto.getId());

        Booking booking = new Booking(null, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2),
                item, null, null);
        BookingDtoReturned bookingDtoReturned =
                bookingService.createBooking(BookingMapper.toBookingDtoReceived(booking), author.getId());
        booking.setId(bookingDtoReturned.getId());
        bookingService.bookingConfirmation(booking.getId(), true, user.getId());

        sleep(6000);

        Comment comment = new Comment(null, "comment", item, author);
        CommentDto commentDto = itemService.commentItem(item.getId(), CommentMapper.toCommentDto(comment),
                author.getId());

        assertEquals(comment.getText(), commentDto.getText());
        assertEquals(comment.getAuthor().getName(), commentDto.getAuthorName());
    }

    @Test
    void emptyCommentItemTest() throws InterruptedException {
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        user.setId(userDto.getId());

        ItemDto itemDto = itemService.createItem(ItemMapper.toItemDto(item), user.getId());
        item.setId(itemDto.getId());

        User author = new User(null, "author", "author@yandex.ru");
        UserDto authorDto = userService.createUser(UserMapper.toUserDto(author));
        author.setId(authorDto.getId());

        Booking booking = new Booking(null, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2),
                item, null, null);
        BookingDtoReturned bookingDtoReturned =
                bookingService.createBooking(BookingMapper.toBookingDtoReceived(booking), author.getId());
        booking.setId(bookingDtoReturned.getId());
        bookingService.bookingConfirmation(booking.getId(), true, user.getId());

        sleep(6000);

        Comment comment = new Comment(null, "", item, author);
        assertThrows(InvalidException.class,
                () -> itemService.commentItem(item.getId(), CommentMapper.toCommentDto(comment), author.getId()));
    }

    @Test
    void commentWrongItemTest() {
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        user.setId(userDto.getId());

        User author = new User(null, "author", "author@yandex.ru");
        UserDto authorDto = userService.createUser(UserMapper.toUserDto(author));
        author.setId(authorDto.getId());

        item.setId(5L);
        Comment comment = new Comment(null, "comment", item, author);
        assertThrows(DataNotFoundException.class,
                () -> itemService.commentItem(item.getId(), CommentMapper.toCommentDto(comment), author.getId()));
    }

    @Test
    void commentItemWrongUserTest() {
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        user.setId(userDto.getId());

        ItemDto itemDto = itemService.createItem(ItemMapper.toItemDto(item), user.getId());
        item.setId(itemDto.getId());

        User author = new User(null, "author", "author@yandex.ru");
        author.setId(5L);

        Comment comment = new Comment(null, "comment", item, author);
        assertThrows(DataNotFoundException.class,
                () -> itemService.commentItem(item.getId(), CommentMapper.toCommentDto(comment), author.getId()));
    }

    @Test
    void doubleCommentItemTest() throws InterruptedException {
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        user.setId(userDto.getId());

        ItemDto itemDto = itemService.createItem(ItemMapper.toItemDto(item), user.getId());
        item.setId(itemDto.getId());

        User author = new User(null, "author", "author@yandex.ru");
        UserDto authorDto = userService.createUser(UserMapper.toUserDto(author));
        author.setId(authorDto.getId());

        Booking booking = new Booking(null, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2),
                item, null, null);
        BookingDtoReturned bookingDtoReturned =
                bookingService.createBooking(BookingMapper.toBookingDtoReceived(booking), author.getId());
        booking.setId(bookingDtoReturned.getId());
        bookingService.bookingConfirmation(booking.getId(), true, user.getId());

        sleep(6000);

        Comment comment = new Comment(null, "comment", item, author);
        itemService.commentItem(item.getId(), CommentMapper.toCommentDto(comment), author.getId());

        assertThrows(InvalidException.class,
                () -> itemService.commentItem(item.getId(), CommentMapper.toCommentDto(comment), author.getId()));
    }

    @Test
    void commentItemWithoutBookingTest() {
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        user.setId(userDto.getId());

        ItemDto itemDto = itemService.createItem(ItemMapper.toItemDto(item), user.getId());
        item.setId(itemDto.getId());

        User author = new User(null, "author", "author@yandex.ru");
        UserDto authorDto = userService.createUser(UserMapper.toUserDto(author));
        author.setId(authorDto.getId());

        Comment comment = new Comment(null, "comment", item, author);

        assertThrows(InvalidException.class, () -> itemService.commentItem(item.getId(),
                CommentMapper.toCommentDto(comment), author.getId()));
    }

    @Test
    void viewAllItemsTest() {
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        ItemDto itemDto = itemService.createItem(ItemMapper.toItemDto(item), user.getId());
        item.setId(itemDto.getId());
        Item item2 = new Item(null, "item2", "description2", true, user, null);
        ItemDto itemDto2 = itemService.createItem(ItemMapper.toItemDto(item2), user.getId());
        item2.setId(itemDto2.getId());
        Item item3 = new Item(null, "item3", "description3", true, user, null);
        ItemDto itemDto3 = itemService.createItem(ItemMapper.toItemDto(item3), user.getId());
        item3.setId(itemDto3.getId());

        List<ItemDtoOwner> itemsDtoOwner = itemService.viewAllItems(user.getId(), 0, 10);

        assertEquals(itemsDtoOwner.size(), 3);
        assertEquals(itemsDtoOwner.get(0).getName(), item.getName());
        assertEquals(itemsDtoOwner.get(1).getName(), item2.getName());
        assertEquals(itemsDtoOwner.get(2).getName(), item3.getName());
    }

    @Test
    void searchItemsTest() {
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        user.setId(userDto.getId());
        ItemDto itemDto = itemService.createItem(ItemMapper.toItemDto(item), user.getId());
        item.setId(itemDto.getId());
        Item item2 = new Item(null, "item2", "descript 2", true, user, null);
        ItemDto itemDto2 = itemService.createItem(ItemMapper.toItemDto(item2), user.getId());
        item2.setId(itemDto2.getId());
        Item item3 = new Item(null, "item3", "description", true, user, null);
        ItemDto itemDto3 = itemService.createItem(ItemMapper.toItemDto(item3), user.getId());
        item3.setId(itemDto3.getId());

        List<ItemDto> searchedItems = itemService.searchItems("description", 0, 10);

        assertEquals(searchedItems.size(), 2);
        assertEquals(searchedItems.get(0).getName(), item.getName());
        assertEquals(searchedItems.get(1).getName(), item3.getName());
    }

    @Test
    void searchItemsEmptyTextTest() {
        assertEquals(Collections.emptyList(), itemService.searchItems("", 0, 10));
    }
}
