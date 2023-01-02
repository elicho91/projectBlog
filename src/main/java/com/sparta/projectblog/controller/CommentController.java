package com.sparta.projectblog.controller;

import com.sparta.projectblog.dto.CommentRequestDto;
import com.sparta.projectblog.dto.CommentResponseDto;
import com.sparta.projectblog.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/api/posts/{post_id}/comments")
    public CommentResponseDto createComment(@PathVariable Long post_id, @RequestBody CommentRequestDto commentRequestDto, HttpServletRequest request) {
        String username = commentRequestDto.getUsername();
        String comment = commentRequestDto.getComment();
        return commentService.createComment(post_id, username, comment, request);
    }

    @PutMapping("/api/posts/{id}/comments/{commentId}")
    public void updateComment(@PathVariable Long id, @PathVariable Long commentId, HttpServletRequest request, @RequestBody CommentRequestDto commentRequestDto) {
        commentService.update(id, commentId, request, commentRequestDto);
    }

    @DeleteMapping("/api/posts/{id}/comments/{commentId}")
    public String deleteComment(@PathVariable Long id, @PathVariable Long commentId,HttpServletRequest request) {
        return commentService.delete(id, commentId, request);
    }
}