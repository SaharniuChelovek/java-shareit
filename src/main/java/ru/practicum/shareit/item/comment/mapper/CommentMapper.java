package ru.practicum.shareit.item.comment.mapper;

import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CreateCommentDto;

import java.time.LocalDateTime;

public class CommentMapper {

    public static Comment toComment(CreateCommentDto dto) {
        Comment comment = new Comment();
        comment.setText(dto.getText());
        comment.setCreated(LocalDateTime.now());
        return comment;
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }
}
