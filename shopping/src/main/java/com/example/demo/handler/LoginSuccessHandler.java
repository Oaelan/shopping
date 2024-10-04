package com.example.demo.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import com.example.demo.entity.RefreshTokenEntity;
import com.example.demo.repository.RefreshTokenRepository;
import com.example.demo.repository.UsersRepository;
import com.example.demo.service.JwtService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 로그인 성공 시 호출되는 핸들러 클래스
 * 사용자가 성공적으로 로그인하면 JWT Access Token과 Refresh Token을 발급하여 응답에 추가하고
 * Refresh Token을 DB에 저장하는 역할을 수행합니다.
 */
@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService; // JWT 서비스 - Access Token 및 Refresh Token 생성 및 전송
    private final UsersRepository usersRepository; // 유저 정보 레포지토리 - Refresh Token 업데이트 시 사용
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.access.expiration}")
    private String accessTokenExpiration; // Access Token 만료 기간 (application.properties 또는 yml에서 설정된 값)

    /**
     * 인증 성공 시 호출되는 메서드
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param authentication 인증 객체 (인증된 사용자 정보 포함)
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException  {
        // 인증된 사용자의 이메일을 추출
        String email = extractUsername(authentication);
        
        // JwtService를 사용해 Access Token과 Refresh Token 생성
        String accessToken = jwtService.createAccessToken(email); 
        String refreshToken = jwtService.createRefreshToken();

        // 생성된 토큰들을 HTTP 응답 헤더에 추가
        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);

        // 사용자의 Refresh Token을 데이터베이스에 저장
        usersRepository.findByEmail(email)
                .ifPresent(user -> {
                	RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                			 .usersEntity(user)
                             .token(refreshToken)
                             .createdAt(LocalDateTime.now())
                             .expiresAt(LocalDateTime.now().plusWeeks(2)) // Refresh Token의 만료 시간을 2주 후로 설정
                             .build();
                    refreshTokenRepository.save(refreshTokenEntity); // RefreshTokenEntity 저장
                });

        // 로그인 성공 로그 남기기
        log.info("로그인에 성공하였습니다. 이메일 : {}", email);
        log.info("로그인에 성공하였습니다. AccessToken : {}", accessToken);
        log.info("발급된 AccessToken 만료 기간 : {}", accessTokenExpiration);
        
        // 로그인 성공 후 리다이렉트할 URL 설정
        setDefaultTargetUrl("/user/login/Success"); // 사용자가 로그인 후 이동할 페이지를 설정합니다.
        
        // 부모 클래스의 성공 처리 로직 호출
        super.onAuthenticationSuccess(request, response, authentication);
    }

    /**
     * 인증 객체에서 사용자 이름(이메일)을 추출하는 메서드
     * @param authentication 인증 객체 (로그인한 사용자 정보 포함)
     * @return 추출된 사용자 이름(이메일)
     */
    private String extractUsername(Authentication authentication) {
        // Authentication 객체에서 UserDetails 객체를 가져옴
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // UserDetails에서 사용자 이름(이메일)을 반환
        return userDetails.getUsername();
    }
}
