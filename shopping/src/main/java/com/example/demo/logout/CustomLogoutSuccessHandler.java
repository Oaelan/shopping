package com.example.demo.logout;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.demo.jwt.JwtService;
import com.example.demo.repository.UsersRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler,LogoutHandler {
	
	private final JwtService jwtService;
	private final UsersRepository userRepository;
	
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
		 // 리프레쉬 토큰 추출
        String refreshToken = jwtService.extractRefreshToken(request).orElse(null);
        if (refreshToken != null) {
            // 유저 테이블에서 해당 리프레쉬 토큰을 삭제
            userRepository.findByRefreshToken(refreshToken).ifPresent(user -> {
                user.updateRefreshToken(null); // 리프레쉬 토큰 삭제
                userRepository.save(user);
            });
        }else {
        	log.info("리프레쉬 토큰 없음");
        }
        
        // 세션 무효화
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
		
	}
}
