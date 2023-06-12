package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Возвращаем комментарии для вещи
    List<Comment> findCommentsByItemIdOrderByCreatedAsc(Long itemId);

    // Возвращаем комментарии для вещей по ids
    List<Comment> findCommentsByItemIsIn(Collection<Item> items);
}
