package com.example.demo.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.UsersDTO;
import com.example.demo.entity.UsersEntity;
import com.example.demo.service.impl.LoginPostServiceImpl;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.servlet.http.*;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController
@RequestMapping("/login/Success/api")
public class LoginRestController {
	
	@Autowired
	LoginPostServiceImpl loginPostServiceImpl;
	
	
	
	@PostMapping("/modify")
	public String modifyPw(@RequestBody String password, HttpSession session) {
		
		String userEmail = (String) session.getAttribute("email");		
		UsersEntity usersEntity = loginPostServiceImpl.getUserInfo(userEmail);	
		loginPostServiceImpl.modifyPw(usersEntity, password);
	
		return "비밀번호가 수정되었습니다!";
	}
	
	@PostMapping("/login")
	 public String login(@RequestBody UsersDTO usersDTO) {
		
		log.info("로그인" + usersDTO.getPassword());
	
		
		return "Failure";
		
//		if(loginPostServiceImpl.login(usersDTO) instanceof UsersEntity) {
//			return "loginSuccess"; // main.html로 매핑
//		}
//		else {
//			return "Failure";
//		}
	}
	
}
