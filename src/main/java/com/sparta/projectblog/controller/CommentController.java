package com.sparta.projectblog.controller;

import com.sparta.projectblog.dto.CommentRequestDto;
import com.sparta.projectblog.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/api/posts/{id}")
    public void createComment(@PathVariable Long id, @RequestBody CommentRequestDto commentRequestDto, HttpServletRequest request) {
        commentService.createComment(id, request, commentRequestDto);
    }

    @PutMapping("/api/posts/{id}/{commentId}")
    public void updateComment(@PathVariable Long id, @PathVariable Long commentId, HttpServletRequest request, CommentRequestDto commentRequestDto) {
        commentService.update(id, commentId, request, commentRequestDto);
    }

    @DeleteMapping("/api/posts/{id}/{commentId}")
    public String deleteComment(@PathVariable Long id, @PathVariable Long commentId, HttpServletRequest request) {  // 토큰을 가져와야하기 때문에 request 객체를 넣어준다.
        return commentService.delete(id, commentId, request);
    }
}