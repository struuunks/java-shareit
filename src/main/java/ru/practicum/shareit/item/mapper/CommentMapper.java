package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class CommentMapper {
    public static Comment toComment(CommentDto commentDto, User user, Item item) {
        return Comment.builder()
                .id(commentDto.getId())
                .text(commentDto.getText())
                .author(user)
                .item(item)
                .build();
    }

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .item(comment.getItem().getName())
                .created(LocalDateTime.now())
                .build();
    }
}
