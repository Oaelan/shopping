package com.example.demo.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.example.demo.repository.UsersRepository;
import com.example.demo.service.JwtService;

import java.io.IOException;

@Slf4j
public class CustomLogoutFilter extends LogoutFilter {

    private final UsersRepository userRepository;
    private final JwtService jwtService;

    // 첫 번째 생성자: LogoutSuccessHandler와 LogoutHandler 배열을 받는 생성자
    public CustomLogoutFilter(LogoutSuccessHandler logoutSuccessHandler, UsersRepository userRepository, JwtService jwtService, LogoutHandler... handlers) {
        super(logoutSuccessHandler, handlers); // 부모 클래스 생성자 호출
        this.userRepository = userRepository; // 필드 초기화
        this.jwtService = jwtService; // 필드 초기화
        setLogoutRequestMatcher(new AntPathRequestMatcher("/logout")); // 로그아웃 경로 설정
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        log.info("CustomLogoutFilter 호출됨");

        // 로그아웃 요청인지 확인
        if (requiresLogout(httpRequest, httpResponse)) {
            // 리프레시 토큰 추출
            String refeshToken = jwtService.extractRefreshToken(httpRequest).orElse(null);
            if (refeshToken != null) {
                // 리프레시 토큰에서 이메일 추출
                String email = jwtService.extractEmail(refeshToken).orElse(null);
                if (email != null) {
                    userRepository.findByEmail(email).ifPresent(user -> {
                        user.updateRefreshToken(null); // 유저 테이블에서 리프레시 토큰 삭제
                        userRepository.save(user);
                        log.info("로그아웃 처리 완료 및 DB 리프레시 토큰 삭제");
                    });
                }
            }
         // 리프레시 토큰 쿠키 삭제
            Cookie refreshCookie = new Cookie("refreshToken", null);
            refreshCookie.setPath("/"); // 쿠키의 유효 경로 설정
            refreshCookie.setHttpOnly(true); // JavaScript에서 접근 불가
            refreshCookie.setMaxAge(0); // 쿠키 만료
            httpResponse.addCookie(refreshCookie); // 쿠키 추가
            log.info("쿠키에서 저장된 리프레시 토큰 삭제");

            // 로그아웃 처리 후 SecurityContextHolder 비우기
            SecurityContextHolder.clearContext();
            log.info("SecurityContextHolder 인증정보 삭제");

            // 로그아웃 성공 핸들러 호출
            getLogoutSuccessHandler().onLogoutSuccess(httpRequest, httpResponse, SecurityContextHolder.getContext().getAuthentication());
            return;
        }

        // 로그아웃 요청이 아니라면 다음 필터로 전달
        chain.doFilter(request, response);
    }

	private LogoutSuccessHandler getLogoutSuccessHandler() {
		// TODO Auto-generated method stub
		return null;
	}
}
