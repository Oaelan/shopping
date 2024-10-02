package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        //return "Sign up form available";
    }

	
    @PostMapping("/signUp")
    public String signUp(@ModelAttribute UsersDTO usersDTO, RedirectAttributes redirectAttributes) {
    	int userId = signUpService.signUp(usersDTO);
    	redirectAttributes.addFlashAttribute("message", "회원가입 성공!");
        return "redirect: main";
        //return "User signed up with ID: " + userId;
    }
}
