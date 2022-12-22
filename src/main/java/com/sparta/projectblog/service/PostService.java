package com.sparta.projectblog.service;

import com.sparta.projectblog.dto.PostRequestDto;
import com.sparta.projectblog.dto.PostResponseDto;
import com.sparta.projectblog.entity.Comment;
import com.sparta.projectblog.entity.Post;
import com.sparta.projectblog.entity.User;
import com.sparta.projectblog.entity.UserRoleEnum;
import com.sparta.projectblog.jwt.JwtUtil;
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
public class PostService {

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;

    private final CommentService commentService;

    @Transactional
    public PostResponseDto createPost(PostRequestDto postRequestDto, HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request);   // 토큰을 가져오는 부분
        Claims claims;  // jwt 안에 들어있는 정보를 담을 수 있는 객체

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

            List<Comment> commentList = commentService.getComments();

            Post post = Post.createPost(
                    postRequestDto.getTitle(), postRequestDto.getUsername(), postRequestDto.getContents(), user.getId(), commentList
            );

            postRepository.saveAndFlush(post);

            return new PostResponseDto(post);
        } else {
            return null;
        }
    }

    @Transactional(readOnly = true)
    public List<Post> getPosts() {
        return postRepository.findAllByOrderByModifiedAtDesc();
    }

    @Transactional(readOnly = true)
    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    @Transactional
    public void update(Long id, HttpServletRequest request, PostRequestDto postRequestDto) {
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

            Post post = postRepository.findById(id).orElseThrow(
                    () -> new IllegalArgumentException("존재하지 않는 글입니다.")
            );

            // 사용자 권한 가져오기
            UserRoleEnum userRoleEnum = user.getRole();
            System.out.println("role = " + userRoleEnum);

            if (userRoleEnum == UserRoleEnum.ADMIN) {
                // 관리자일 경우
                post.update(postRequestDto);

            } else if (userRoleEnum == UserRoleEnum.USER && user.getUsername().equals(post.getUsername())) { // 사용자 권한이 USER일 경우
                post.update(postRequestDto);
            } else { // 본인의 글이 아닐 경우
                throw new IllegalArgumentException("본인의 글이 아닙니다.");
            }
        }
    }

    @Transactional
    public String delete(Long id, HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request);
        Claims claims;

        if (token != null) {
            if (jwtUtil.validateToken(token)) {
                claims = jwtUtil.getUserInfoFromToken(token);
                User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                        () -> new IllegalArgumentException("존재하지 않는 사용자입니다.")
                );

                Post post = postRepository.findById(id).orElseThrow(
                        () -> new IllegalArgumentException("존재하지 않는 글입니다.")
                );

                // 사용자 권한 가져오기
                UserRoleEnum userRoleEnum = user.getRole();
                System.out.println("role = " + userRoleEnum);

                if (userRoleEnum == UserRoleEnum.ADMIN) {
                    // 관리자일 경우
                    postRepository.deleteById(id);

                } else if (userRoleEnum == UserRoleEnum.USER && user.getUsername().equals(post.getUsername())) { // 사용자 권한이 USER일 경우
                    postRepository.deleteById(id);
                } else { // 본인의 글이 아닐 경우
                    throw new IllegalArgumentException("본인의 글이 아닙니다.");
                }
            }
            return "삭제되었습니다.";
        } else {
            throw new IllegalArgumentException("토큰이 유효하지 않습니다. StatusCode 400");
        }
    }
}
