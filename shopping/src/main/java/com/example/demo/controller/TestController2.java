package com.example.demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController2 {

	@GetMapping("/")
	public String goIndex(Model model) {
		return "main";
	}

	@GetMapping("/Failure")
	public String loginFailure() {
		return "loginFailure";
	}

	@GetMapping("/login")
	public String goLogin() {
		return "loginPage";
	}

}
