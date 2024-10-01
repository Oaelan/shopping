package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class TestController2 {
	
	@GetMapping("/")
	public String goIndex() {
		return "main";
	}
	
	@GetMapping("/Failure")
	public String loginFailure() {
		return "loginFailure";
	}
}
