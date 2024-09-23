package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class TestController {

	@GetMapping("/loginSuccess")
	public String loginSuccess(@RequestParam String param) {
		return "loginSuccess";
	}
	
	
	
	@GetMapping("/loginFailure")
	public String loginFailure(@RequestParam String param) {
		return "loginFailure";
	}
}
