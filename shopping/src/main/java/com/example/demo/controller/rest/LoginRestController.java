package com.example.demo.controller.rest;

import java.util.Optional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.UsersDTO;
import com.example.demo.entity.UsersEntity;
import com.example.demo.service.JwtService;
import com.example.demo.service.impl.LoginPostServiceImpl;


import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user/login/")
public class LoginRestController {
	

	private final LoginPostServiceImpl loginPostServiceImpl;
	private final JwtService jwtService;
	
	
	
	@PostMapping("/userInfo")
	public ResponseEntity<String> loginedUser(HttpServletRequest request) {
		
		// 현재 인증된 사용자 정보를 가져오는 방법
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null && authentication.isAuthenticated()) {
		    String email = authentication.getName(); // 사용자 이름 (이메일)을 가져옴	  
		    return ResponseEntity.ok(email); // 200 OK 응답과 함께 이메일 반환
		}
		return null;
	}
	
	@PostMapping("/modify")
	public String modifyPw(@RequestBody String password, HttpSession session) {
		
		String userEmail = (String) session.getAttribute("email");		
		UsersEntity usersEntity = loginPostServiceImpl.getUserInfo(userEmail);	
		loginPostServiceImpl.modifyPw(usersEntity, password);
	
		return "비밀번호가 수정되었습니다!";
	}
	
}
