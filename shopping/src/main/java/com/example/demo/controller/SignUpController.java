package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.dto.UsersDTO;
import com.example.demo.service.SignUpService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SignUpController {

	private final SignUpService signUpService;
	
	@GetMapping("/signUp")
    public String signUpForm() {
        return "signUp";
    }

	
    @PostMapping("/signUp")
    public String signUp(UsersDTO usersDTO) {
        signUpService.signUp(usersDTO);
        return "index";
    }
}
