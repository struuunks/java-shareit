package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOwner;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @MockBean
    private ItemServiceImpl itemService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private final User user = new User(1L, "User", "user@yandex.ru");
    private final UserDto userDto = UserMapper.toUserDto(user);
    private final Item item = new Item(1L, "item", "description", true, user, null);
    private final ItemDtoOwner itemDtoOwner = ItemMapper.toItemDtoOwner(item);
    private final ItemDto itemDto = ItemMapper.toItemDto(item);
    private final Comment comment = new Comment(1L, "comment", item, user);
    private final CommentDto commentDto = CommentMapper.toCommentDto(comment);

    @Test
    void createItemTest() throws Exception {
        when(itemService.createItem(any(), anyLong())).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class));

        verify(itemService, times(1))
                .createItem(any(), anyLong());
    }

    @Test
    void commentItemTest() throws Exception{
        when(itemService.commentItem(anyLong(), any(), any())).thenReturn(commentDto);

        mockMvc.perform(post("/items/{id}/comment", itemDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText()), String.class))
                .andExpect(jsonPath("$.authorName", is(userDto.getName()), String.class));
    }


    @Test
    void updateItemTest() throws Exception{
        itemDto.setDescription("new description");
        when(itemService.updateItem(anyLong(), any(), anyLong())).thenReturn(itemDto);

        mockMvc.perform(patch("/items/" + itemDto.getId())
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class));
    }

    @Test
    void viewItemInformationTest() throws Exception{
        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(itemDtoOwner);

        mockMvc.perform(get("/items/" + itemDto.getId())
                        .header("X-Sharer-User-Id", userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoOwner.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoOwner.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDtoOwner.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDtoOwner.getAvailable()), Boolean.class));

        verify(itemService, times(1))
                .getItemById(itemDto.getId(), userDto.getId());
    }
    @Test
    void viewAllItemsTest() throws Exception {
        when(itemService.viewAllItems(anyLong(), anyInt(), anyInt())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(itemService, times(1))
                .viewAllItems(anyLong(), anyInt(), anyInt());
    }

    @Test
    void searchItemsTest() throws Exception {
        when(itemService.searchItems(anyString(), anyInt(), anyInt())).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search").param("text", "item1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName()), String.class));

        verify(itemService, times(1))
                .searchItems(anyString(), anyInt(), anyInt());
    }
}