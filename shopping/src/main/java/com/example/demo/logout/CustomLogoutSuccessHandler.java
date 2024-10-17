package com.example.demo.logout;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler,LogoutHandler {

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException{
		log.info("onLogoutSuccess: 로그아웃 성공: 사용자 인증 정보가 삭제되었습니다.");
           
        // 로그아웃 성공 후 리다이렉트
        response.sendRedirect("/");
	}
	
	@Override
	public 	void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		log.info("logout: 사용자가 로그아웃 하였습니다.");	
	}
}
