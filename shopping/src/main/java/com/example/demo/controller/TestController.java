package com.example.demo.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;



@Controller
public class TestController {

	public String loginSuccess(Model model, @AuthenticationPrincipal OAuth2User oAuth2User) {
	    // OAuth2User로부터 사용자 이름 속성을 가져옴
	    String name = oAuth2User.getAttribute("name");

	    // "name"이라는 이름으로 사용자 이름을 모델에 추가
	    model.addAttribute("name", name);

	    // loginSuccess.html로 이동
	    return "loginSuccess";
	}

	
	
	
	@GetMapping("/loginFailure")
	public String loginFailure(@RequestParam String param) {
		return "loginFailure";
	}
}
