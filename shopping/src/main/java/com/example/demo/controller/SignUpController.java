package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.UsersDTO;
import com.example.demo.service.SignUpService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SignUpController {

	private final SignUpService signUpService;
	
	@GetMapping("/signUp")
    public String signUpForm() {
        //return "signUp";
        return "Sign up form available";
    }

	
    @PostMapping("/signUp")
    public String signUp(@RequestBody UsersDTO usersDTO) {
    	int userId = signUpService.signUp(usersDTO);
        //return "index";
        return "User signed up with ID: " + userId;
    }
}
