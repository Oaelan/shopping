package com.example.demo.controller;

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

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ui.Model;


@Controller
@Slf4j
@RequestMapping("/user/login")
public class LoginGetController {
	
	
	@PostMapping("/Success")
	public String loginSuccess(Model model) {
	    // 현재 인증된 사용자 정보를 가져오기 위해 Spring Security의 SecurityContextHolder를 사용합니다.
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

	    // 사용자가 인증되지 않았거나 인증 객체가 없는 경우 예외를 던집니다.
	    if (authentication == null) {
	        throw new IllegalStateException("No authenticated user found");
	    }

	    // 인증 객체의 주요 정보가 OAuth2User 타입인지 검사하여 소셜 로그인 사용자 여부를 확인합니다.
	    if (authentication.getPrincipal() instanceof OAuth2User) {
	        // OAuth2User 타입이라면 소셜 로그인 사용자로 간주하여 처리합니다.
	        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
	        String name = oAuth2User.getName(); // 사용자의 이름을 가져옴
	        model.addAttribute("name", name); // 모델에 사용자 이름을 추가하여 뷰에 전달
	        log.info("소셜 로그인 사용자 이름: " + name);
	    } else if (authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
	        // 폼 로그인을 통해 인증된 사용자라면 UserDetails 타입으로 캐스팅합니다.
	        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
	        String username = user.getUsername(); // 사용자 이름(이메일)을 가져옴
	        model.addAttribute("name", username); // 모델에 사용자 이름을 추가하여 뷰에 전달
	        log.info("폼 로그인 사용자 이메일: " + username);
	    } else {
	        // 인증된 사용자가 소셜 로그인 또는 폼 로그인 사용자도 아닌 경우 예외를 던집니다.
	        throw new IllegalStateException("Unsupported authentication principal type");
	    }

	    // 로그인 성공 후 리다이렉트될 페이지를 반환합니다.
	    return "loginSuccess"; // loginSuccess.html로 매핑 (로그인 성공 후 페이지)
	}

	
	@GetMapping("/modify")
	public String goModify() {
		return "modifyUserInfo";
	}
	
	
	
}
