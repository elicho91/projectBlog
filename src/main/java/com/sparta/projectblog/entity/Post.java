package com.sparta.projectblog.entity;

import com.sparta.projectblog.dto.PostRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "post")
public class Post extends Timestamped {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    private String title;


    private String username;


    private String contents;

    @OneToMany(mappedBy = "post")// 연관관계 설정, 비어있는 코멘트 리스트 생성
    private List<Comment> commentList = new ArrayList<>();

    @Builder
    public Post(String title, String username, String contents, Long userId, List<Comment> commentList) {
        this.title = title;
        this.username = username;
        this.contents = contents;
        this.userId = userId;
        this.commentList = commentList;
    }

    public static Post createPost(String title, String username, String contents, Long userId, List<Comment> commentList) {
        return Post.builder()
                .userId(userId)
                .title(title)
                .username(username)
                .contents(contents)
                .commentList(commentList)
                .build();
    }

    public void putCommentList(Comment comment) {
        commentList.add(comment);
    }

    public void update(PostRequestDto postRequestDto) {
        this.title = postRequestDto.getTitle();
        this.username = postRequestDto.getUsername();
        this.contents = postRequestDto.getContents();
    }
}
