package com.sparta.projectblog.entity;

import com.sparta.projectblog.dto.CommentRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "comment")
public class Comment extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(nullable = false)
    private Long userId;

    @Column
    private String username;

    @Column
    private String comment;

    @Builder
    public Comment( Long userId, String username, String comment, Post post) {
        this.userId = userId;
        this.username = username;
        this.comment = comment;
        this.post = post;
    }

    public static Comment createComment(Long userId, String username, String comment, Post post) {
        return Comment.builder()
                .userId(userId)
                .username(username)
                .comment(comment)
                .post(post)
                .build();
    }

    public void update(CommentRequestDto commentRequestDto) {
        this.username = commentRequestDto.getUsername();
        this.comment = commentRequestDto.getComment();
    }
}
