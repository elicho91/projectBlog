package com.sparta.projectblog.dto;

import com.sparta.projectblog.entity.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostResponseDto {

    private Long id;
    private String title;
    private String contents;

    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.contents = post.getContents();
    }
}