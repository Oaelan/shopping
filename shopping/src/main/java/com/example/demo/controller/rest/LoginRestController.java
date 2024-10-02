package com.example.demo.controller.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.UsersDTO;
import com.example.demo.entity.UsersEntity;
import com.example.demo.service.impl.LoginPostServiceImpl;


import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/login/Success/api")
public class LoginRestController {
	

	private final LoginPostServiceImpl loginPostServiceImpl;
	
	
	
	@PostMapping("/modify")
	public String modifyPw(@RequestBody String password, HttpSession session) {
		
		String userEmail = (String) session.getAttribute("email");		
		UsersEntity usersEntity = loginPostServiceImpl.getUserInfo(userEmail);	
		loginPostServiceImpl.modifyPw(usersEntity, password);
	
		return "비밀번호가 수정되었습니다!";
	}
	
}
