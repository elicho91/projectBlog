package com.sparta.projectblog.service;

import com.sparta.projectblog.dto.CommentRequestDto;
import com.sparta.projectblog.entity.Comment;
import com.sparta.projectblog.entity.Post;
import com.sparta.projectblog.entity.User;
import com.sparta.projectblog.entity.UserRoleEnum;
import com.sparta.projectblog.jwt.JwtUtil;
import com.sparta.projectblog.repository.CommentRepository;
import com.sparta.projectblog.repository.PostRepository;
import com.sparta.projectblog.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    private final PostRepository postRepository;

    private final JwtUtil jwtUtil;

    @Transactional
    public void createComment(Long postId, HttpServletRequest request, CommentRequestDto commentRequestDto) {
        String token = jwtUtil.resolveToken(request);
        Claims claims;

        // 올바른 토큰인지 확인
        if (token != null) {
            if (jwtUtil.validateToken(token)) {
                claims = jwtUtil.getUserInfoFromToken(token);
            } else {
                throw new IllegalArgumentException("토큰이 유효하지 않습니다. StatusCode 400");
            }

            // 토큰에서 가져온 사용자 정보를 사용하여 DB 조회
            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new IllegalArgumentException("사용자가 존재하지 않습니다.")
            );

            Post post = postRepository.findById(postId).orElseThrow(
                    () -> new IllegalArgumentException("올바르지 않은 글 번호입니다.")
            );

            Comment comment = Comment.createComment(
                    user.getId(), commentRequestDto.getUsername(), commentRequestDto.getComment(), post
            );

            post.putCommentList(comment);
        }
    }

    @Transactional(readOnly = true)
    public List<Comment> getComments() {
        return commentRepository.findAllByOrderByModifiedAtDesc();
    }

    @Transactional(readOnly = true)
    public Optional<Comment> getCommentById(Long id) {
        return commentRepository.findById(id);
    }

    @Transactional
    public void update(Long id, Long commentId, HttpServletRequest request, CommentRequestDto commentRequestDto) {
        String token = jwtUtil.resolveToken(request);
        Claims claims;

        if(token != null) {
            if (jwtUtil.validateToken(token)) {
                claims = jwtUtil.getUserInfoFromToken(token);
            } else {
                throw new IllegalArgumentException("토큰이 유효하지 않습니다. StatusCode 400");
            }

            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new IllegalArgumentException("존재하지 않는 사용자입니다.")
            );

            Comment comment = commentRepository.findById(commentId).orElseThrow(
                    () -> new IllegalArgumentException("존재하지 않는 댓글입니다.")
            );

            // 사용자 권한 가져오기
            UserRoleEnum userRoleEnum = user.getRole();
            System.out.println("role = " + userRoleEnum);

            if (userRoleEnum == UserRoleEnum.ADMIN) {
                // 관리자일 경우
                comment.update(commentRequestDto);

            } else if (userRoleEnum == UserRoleEnum.USER && user.getUsername().equals(comment.getUsername())) { // 사용자 권한이 USER일 경우
                comment.update(commentRequestDto);
            } else { // 본인의 글이 아닐 경우
                throw new IllegalArgumentException("본인의 댓글이 아닙니다.");
            }
        }
    }

    @Transactional
    public String delete(Long id, Long commentId, HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request);
        Claims claims;

        if (token != null) {
            if (jwtUtil.validateToken(token)) {
                claims = jwtUtil.getUserInfoFromToken(token);
                User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                        () -> new IllegalArgumentException("존재하지 않는 사용자입니다.")
                );

                Comment comment = commentRepository.findById(commentId).orElseThrow(
                        () -> new IllegalArgumentException("존재하지 않는 댓글입니다.")
                );

                // 사용자 권한 가져오기
                UserRoleEnum userRoleEnum = user.getRole();
                System.out.println("role = " + userRoleEnum);

                if (userRoleEnum == UserRoleEnum.ADMIN) {
                    // 관리자일 경우
                    commentRepository.deleteById(commentId);

                } else if (userRoleEnum == UserRoleEnum.USER && user.getUsername().equals(comment.getUsername())) { // 사용자 권한이 USER일 경우
                    commentRepository.deleteById(commentId);
                } else { // 본인의 글이 아닐 경우
                    throw new IllegalArgumentException("본인의 댓글이 아닙니다.");
                }
            }
            return "삭제되었습니다.";
        } else {
            throw new IllegalArgumentException("토큰이 유효하지 않습니다. StatusCode 400");
        }
    }
}
