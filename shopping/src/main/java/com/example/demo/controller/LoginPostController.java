package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.controller.rest.LoginRestController;
import com.example.demo.dto.UsersDTO;
import com.example.demo.entity.UsersEntity;
import com.example.demo.service.LoginPostService;
import com.example.demo.service.impl.LoginPostServiceImpl;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class LoginPostController {

	@Autowired
	LoginPostService loginPostService;

	@PostMapping("/loginUser")
	public String login(@RequestBody UsersDTO usersDTO,HttpSession session) {
		log.info("로그인 컨트롤러 실행");
		
		try {
	        UsersDTO foundUser = loginPostService.login(usersDTO);
	        session.setAttribute("email", foundUser.getEmail());
	        return "loginSuccess"; // 로그인 성공 시
	    } catch (IllegalArgumentException e) {
	        // 로그인 실패 시, 실패 페이지 반환
	        log.error("로그인 실패: " + e.getMessage());
	        return "redirect:/Failure";
	    }
	}
		
}
