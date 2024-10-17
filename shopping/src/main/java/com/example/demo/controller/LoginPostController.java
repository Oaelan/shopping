package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.service.LoginPostService;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class LoginPostController {

	@Autowired
	LoginPostService loginPostService;

	@GetMapping("/token-save")
    public String handleNaverLogin() {
        // 리다이렉트 시 해당 페이지로 이동하게 함
        return "token-save"; // token-save.html 파일로 리다이렉트
    }
		
}
