package com.example.demo.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.CustomOAuth2User;
import com.example.demo.service.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ui.Model;


@Controller
@Slf4j
@RequestMapping("/user/login")
public class LoginGetController {
	

	@GetMapping("/Success")
	public String loginSuccess() {
	    // 로그인 성공 후 리다이렉트될 페이지를 반환합니다.
	    return "loginSuccess"; // loginSuccess.html로 매핑 (로그인 성공 후 페이지)
	}

	
	@GetMapping("/modify")
	public String goModify() {
		return "modifyUserInfo";
	}
	
	
	
}
