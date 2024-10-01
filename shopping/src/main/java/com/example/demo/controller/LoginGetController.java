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

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ui.Model;


@Controller
@Slf4j
@RequestMapping("/user/login")
public class LoginGetController {
	
	
	@GetMapping("/Success")
	public String loginSuccess(Model model,HttpSession session) {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    //이 줄에서는 Spring Security가 관리하는 현재 사용자 인증 정보를 가져옵니다.
	    //SecurityContextHolder는 현재 사용자와 관련된 SecurityContext를 저장하고 있습니다.
	    //getContext().getAuthentication()을 호출하여 현재 사용자의 Authentication 객체를 가져옵니다.
	    //이 객체는 현재 사용자가 인증되었는지, 어떤 권한이 있는지 등의 정보를 가지고 있습니다.
	    
	    if (authentication == null || !(authentication.getPrincipal() instanceof OAuth2User)) {
	    	//authentication이 null이라는 것은 현재 사용자가 인증되지 않았다는 뜻입니다.
	    	//authentication.getPrincipal()은 현재 인증된 사용자의 주요 정보를 반환합니다.
	    	//instanceof OAuth2User를 사용하여 반환된 객체가 OAuth2User 타입인지 확인합니다.
	        throw new IllegalStateException("No authenticated user found");
	        // 두 가지 조건 중 하나라도 만족하면,
	        //즉 사용자가 인증되지 않았거나 올바른 타입의 인증 객체가 아니라면 IllegalStateException을 던집니다.
	    }
	    
	    CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

	    // "name" 속성을 가져오는 방식으로 수정
		String name = oAuth2User.getName();
	    model.addAttribute("name", name);
	   
	    log.info(name);
	    return "loginSuccess"; // main.html로 매핑
	}
	
	@GetMapping("/modify")
	public String goModify() {
		return "modifyUserInfo";
	}
	
	
	
}
