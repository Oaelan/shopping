package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.dto.UsersDTO;
import com.example.demo.entity.UsersEntity;
import com.example.demo.service.impl.LoginPostServiceImpl;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@Controller
public class LoginPostController {

	@Autowired
	LoginPostServiceImpl loginPostServiceImpl;

//	@PostMapping("/login")
//	public String login(@RequestBody UsersDTO usersDTO) {
//		if(loginPostServiceImpl.login(usersDTO) instanceof UsersEntity) {
//			return "loginSuccess"; // main.html로 매핑
//		}
//		else {
//			return "redirect:/Failure";
//		}
//		
//	}

}
