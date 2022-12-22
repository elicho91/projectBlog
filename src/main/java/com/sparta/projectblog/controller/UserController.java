package com.sparta.projectblog.controller;

import com.sparta.projectblog.dto.LoginRequestDto;
import com.sparta.projectblog.dto.SignupRequestDto;
import com.sparta.projectblog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class UserController {

    private final UserService userService;

    @ResponseBody
    @PostMapping("/signup")
    public String signup(@RequestBody SignupRequestDto signupRequestDto) {
        System.out.println("아이디를 입력해주세요, 최소 4자 이상 10자 이하, 알파벳 소문자, 숫자");
        System.out.println("비밀번호를 입력해주세요, 최소 8자 이상 15자 이하, 알파벳 대소문자, 숫자,특수문자");
        userService.signup(signupRequestDto);
        return "success";
    }

    @ResponseBody // ajax 형식으로 body를 받아오기 떄문에 body 어노테이션을 써줘야한다.
    @PostMapping("/login") // request에서 헤더가 넘어온 것을 받아오는 것처럼 토큰을 반환할때는 response 객체로 반환,
    public String login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        userService.login(loginRequestDto, response);
        return "success";
    }

}