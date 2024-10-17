package com.example.demo.logout;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import com.example.demo.entity.UsersEntity;
import com.example.demo.jwt.JwtService;
import com.example.demo.repository.UsersRepository;


import io.jsonwebtoken.ExpiredJwtException;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final UsersRepository userRepository;
    private final JwtService jwtService;
    private static final String CHECK_URL = "/logout";
    

    @Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
	}
    
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
    	
    	
    	
    	// 로그아웃 시 요청 uri;
    	String rquestUrl = request.getRequestURI();   	
    	// "/logout" 아닌 요청이라면 다음 필터로 넘
    	if(!rquestUrl.matches(CHECK_URL)){
    		chain.doFilter(request, response);
    		return;  		
    	}
    	
    	log.info("CustomLogoutFilter 호출됨");
    	
    	//요청 메서드 타입 확인
    	String requsetMethod = request.getMethod();    	
    	// post 요청이 아니라면 다음 필터로 넘김 
    	if(!requsetMethod.equals("POST")) {
    		chain.doFilter(request, response);
    		return; 
    	}
    	
    	String refresh = jwtService.extractRefreshToken(request).orElse(null);
    	//String access = jwtService.extractAccessToken(request).orElse(null);
    	
    	
    	// 리프레쉬 토큰 쿠키 유무 체크
    	if(refresh == null) {
    		log.info("refresh 토큰이 존재하지 않습니다");
    		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    		return;
    	}
    	
    	// 리프레시 토큰 유효성 검사 및 로그아웃 처리
        try {
            jwtService.isTokenValid(refresh);  // 리프레시 토큰 유효성 검사
            
            // 유저 테이블에서 해당 리프레시 토큰 조회 및 삭제
            userRepository.findByRefreshToken(refresh).ifPresentOrElse(
                user -> {
                    user.updateRefreshToken(null);
                    userRepository.save(user);
                    log.info("로그아웃 처리 완료 및 DB 리프레시 토큰 삭제");
                    clearCookiesAndSecurityContext(response); // 쿠키 및 인증 정보 삭제
                },
                () -> {
                    log.error("유효하지 않은 리프레시 토큰입니다.");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }
            );

        } catch (ExpiredJwtException e) {
            log.error("리프레시 토큰이 만료되었습니다.");
            clearCookiesAndSecurityContext(response);  // 만료된 경우에도 쿠키와 인증 정보 삭제
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
    
    // 쿠키와 SecurityContextHolder 인증 정보 초기화
    private void clearCookiesAndSecurityContext(HttpServletResponse response) {
        clearCookie(response, "Authorization-refresh");
        SecurityContextHolder.clearContext();
        log.info("쿠키와 SecurityContextHolder 인증정보 삭제 완료");
    }

    // 쿠키 삭제 메서드
    private void clearCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setPath("/");  // 쿠키 유효 경로 설정
        cookie.setMaxAge(0);  // 즉시 만료
        response.addCookie(cookie);  // 쿠키 추가
        log.info("{} 쿠키 삭제 완료", cookieName);
    }

}
