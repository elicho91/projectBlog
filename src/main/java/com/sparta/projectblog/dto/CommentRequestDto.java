package com.sparta.projectblog.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CommentRequestDto {
    String username;
    String comment;
}