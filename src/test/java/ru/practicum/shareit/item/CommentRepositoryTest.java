package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.CommentRepository;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindCommentByItemId() {
        User owner = new User(3L, "user", "user@yandex.ru");
        owner = userRepository.save(owner);

        User author = new User(4L, "user2", "user2@yandex.ru");
        author = userRepository.save(author);

        Item item = new Item(1L, "item1", "First description", true, owner, null);
        item = itemRepository.save(item);

        Comment comment1 = new Comment(1L, "first comment", item, author);
        comment1 = commentRepository.save(comment1);

        Comment comment2 = new Comment(2L, "second comment", item, author);
        comment2 = commentRepository.save(comment2);

        List<Comment> foundComments = commentRepository.findCommentByItemId(item.getId());

        assertEquals(2, foundComments.size());

        assertEquals(foundComments.get(0).getId(), comment1.getId());
        assertEquals(foundComments.get(0).getText(), comment1.getText());
        assertEquals(foundComments.get(0).getItem().getId(), comment1.getItem().getId());
        assertEquals(foundComments.get(0).getAuthor().getId(), comment1.getAuthor().getId());

        assertEquals(foundComments.get(1).getId(), comment2.getId());
        assertEquals(foundComments.get(1).getText(), comment2.getText());
        assertEquals(foundComments.get(1).getItem().getId(), comment2.getItem().getId());
        assertEquals(foundComments.get(1).getAuthor().getId(), comment2.getAuthor().getId());
    }
}
