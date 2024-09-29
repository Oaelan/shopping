package com.example.demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.CustomOAuth2User;

import lombok.extern.slf4j.Slf4j;

import org.springframework.ui.Model;


@Controller
@Slf4j
@RequestMapping("/login")
public class TestController {
	
	@GetMapping("/Success")
	public String loginSuccess(Model model) {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    if (authentication == null || !(authentication.getPrincipal() instanceof OAuth2User)) {
	        throw new IllegalStateException("No authenticated user found");
	    }

	    CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

	    // "name" 속성을 가져오는 방식으로 수정
		String name = oAuth2User.getName();
	    model.addAttribute("name", name);
	   
	   System.out.println(name);
	    return "loginSuccess"; // main.html로 매핑
	}


	
	
	
	@GetMapping("/Failure")
	public String loginFailure() {
		return "loginFailure";
	}
	
	
	
}
