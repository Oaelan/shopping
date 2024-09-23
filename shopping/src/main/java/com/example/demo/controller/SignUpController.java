package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SignUpController {

	@GetMapping("/signUp")
    public String signUpForm() {
        return "signUp";
    }

    /**
     * 회원가입 진행
     * @param user
     * @return
     */
    @PostMapping("/signUp")
    public String signUp(UserDTO userDTO) {
        userService.joinUser(userDTO);
        return "redirect:/login"; //로그인 구현 예정
    }
}
