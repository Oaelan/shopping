package com.example.demo.handler;

import java.io.IOException;
import java.net.URLEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.demo.entity.CustomOAuth2User;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor

public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler{
	
	@Override
	public void onAuthenticationSuccess(
			HttpServletRequest request, HttpServletResponse response,
			Authentication authentication
			)throws IOException, ServletException {
		
		CustomOAuth2User oAuth2User = (CustomOAuth2User)authentication.getPrincipal();
	
		String name = oAuth2User.getName();
		
		response.sendRedirect("/login/Success?name=" + URLEncoder.encode(name, "UTF-8"));

		
	}
}
