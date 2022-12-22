package com.sparta.projectblog.dto;

import com.sparta.projectblog.entity.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentResponseDto {
    private Long id;

    private Long userId;

    private Long postId;

    private String username;

    private String comment;

    public CommentResponseDto(Long userId, Long postId, Comment comment) {
        this.id = comment.getId();
        this.userId = userId;
        this.postId = postId;
        this.username = comment.getUsername();
        this.comment = comment.getComment();
    }
}