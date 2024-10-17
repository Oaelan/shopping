package com.example.demo.oauth2;

import java.io.IOException;
import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.demo.jwt.JwtService;
import com.example.demo.oauth2.CustomOAuth2User;
import com.example.demo.repository.UsersRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
// 이 클래스는 소셜 로그인 성공 시 호출되는 성공 핸들러입니다.
@Component // 이 클래스가 Spring의 빈으로 등록되도록 지정합니다.
@RequiredArgsConstructor // 모든 final 필드에 대해 생성자를 자동으로 생성합니다. (현재 사용되는 필드는 없습니다.)

public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final JwtService jwtService;
    private final UsersRepository userRepository;
    
    @Value("${jwt.access.expiration}")
    private String accessTokenExpiration;
    
   
    // 인증 성공 시 실행되는 메서드
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response,
            Authentication authentication // 인증 객체, 인증된 사용자 정보를 담고 있습니다.
    ) throws IOException, ServletException {
    	//인증된 객체를 SecurityContextHolder 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 인증된 사용자의 정보를 가져옵니다.
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        
        log.info("OAuth2SuccessHandler 실행:" + oAuth2User.toString());
        // 사용자 이름을 가져옵니다.
        String name = oAuth2User.getName();
        // 사용자 이름을 가져옵니다.
        String email = oAuth2User.getEmail();
        
        // 우리 서버에서 유저에게 발급하는 토큰 생성
        String accessToken = jwtService.createAccessToken(email); // JwtService의 createAccessToken을 사용하여 AccessToken 발급
        String refreshToken = jwtService.createRefreshToken(); // JwtService의 createRefreshToken을 사용하여 RefreshToken 발급
      
        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    user.updateRefreshToken(refreshToken);
                    userRepository.saveAndFlush(user);
                });
        
        log.info("로그인에 성공하였습니다. 이메일 : {}", email);
        log.info("로그인에 성공하였습니다. AccessToken : {}", accessToken);
        log.info("발급된 AccessToken 만료 기간 : {}", accessTokenExpiration);
        log.info("로그인에 성공하였습니다. refreshToken : {}", refreshToken);
       
        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken); // 응답 헤더에 AccessToken, RefreshToken 실어서 응답
        
        log.info("OAuth2 로그인 성공: {}", email);
        // 메인 페이지로 바로 리다이렉트하는 대신, 임시 페이지로 이동
        response.sendRedirect("/");

    }
}
