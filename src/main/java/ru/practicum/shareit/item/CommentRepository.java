package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Возвращаем комментарии для вещи
    List<Comment> findCommentsByItem_Id(Long itemId);
}
