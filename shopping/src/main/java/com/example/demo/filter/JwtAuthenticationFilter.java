package com.example.demo.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.entity.UsersEntity;
import com.example.demo.repository.UsersRepository;
import com.example.demo.service.JwtService;
import com.example.demo.until.PasswordUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Jwt 인증 필터 "/login" 이외의 URI 요청이 왔을 때 처리하는 필터
 *
 * 기본적으로 사용자는 요청 헤더에 AccessToken만 담아서 요청 AccessToken 만료 시에만 RefreshToken을 요청 헤더에
 * AccessToken과 함께 요청
 *
 * 1. RefreshToken이 없고, AccessToken이 유효한 경우 -> 인증 성공 처리, RefreshToken을 재발급하지는
 * 않는다. 2. RefreshToken이 없고, AccessToken이 없거나 유효하지 않은 경우 -> 인증 실패 처리, 403 ERROR
 * 3. RefreshToken이 있는 경우 -> DB의 RefreshToken과 비교하여 일치하면 AccessToken 재발급,
 * RefreshToken 재발급(RTR 방식) 인증 성공 처리는 하지 않고 실패 처리
 */
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private static final String CHECK_URL = "/user/login/"; // "/login"으로 들어오는 요청은 Filter 작동 X
	private static final String CHECK_URL1 ="/"; 
	private final JwtService jwtService;
	private final UsersRepository userRepository;

	//private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		log.info("요청 URI: {}", request.getRequestURI()); // 추가
		
		if (request.getRequestURI().startsWith(CHECK_URL)) {

		String refreshToken = jwtService.extractRefreshToken(request).filter(jwtService::isTokenValid).orElse(null);
		
		
		// 액세스 토큰이 유효하지 않은 경우
		// 클라이언트에서 리프레쉬토큰을 헤더에 넣어 다시 요청
		if (refreshToken != null) {
			//아래의 메소드를 호출하여 액세스/리프레쉬 토큰 재발급 
			checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
			return;
		}

		// 액세스 토큰이 유효한 경우
		if (refreshToken == null) {
			
			checkAccessTokenAndAuthentication(request, response, filterChain);
		}
	  }else {
		  log.info("JwtAuthenticationFilter 건뜀ㅇㅇ");
		  filterChain.doFilter(request, response);
	  }
	}

	public void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
		userRepository.findByRefreshToken(refreshToken).ifPresent(user -> {
			
			String reIssuedRefreshToken = reIssueRefreshToken(user);
			// 재발급 받은 토큰 리스폰 헤더 설정
			jwtService.sendAccessAndRefreshToken(response, jwtService.createAccessToken(user.getEmail()),
					reIssuedRefreshToken);
		});
	}

	private String reIssueRefreshToken(UsersEntity user) {
		// 리프레쉬 토큰 재발급
		String reIssuedRefreshToken = jwtService.createRefreshToken();
		// 리프레쉬 토큰 컬럼열 업데이트
		user.updateRefreshToken(reIssuedRefreshToken);
		userRepository.saveAndFlush(user);
		return reIssuedRefreshToken;
	}

	public void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response,
	        FilterChain filterChain) throws ServletException, IOException {
	    log.info("checkAccessTokenAndAuthentication() 호출");

	    jwtService.extractAccessToken(request)
	        .filter(jwtService::isTokenValid)
	        .ifPresent(accessToken -> {
	            jwtService.extractEmail(accessToken)
	                .ifPresent(email -> userRepository.findByEmail(email).ifPresent(this::saveAuthentication));
	        });

	    filterChain.doFilter(request, response);
	}


	public void saveAuthentication(UsersEntity myUser) {
		String password = myUser.getPassword();
		if (password == null) { // 소셜 로그인 유저의 비밀번호 임의로 설정 하여 소셜 로그인 유저도 인증 되도록 설정
			password = PasswordUtil.generateRandomPassword();
		}

		UserDetails userDetailsUser = org.springframework.security.core.userdetails.User.builder()
				.username(myUser.getEmail()).password(password).roles(myUser.getRole()).build();

		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetailsUser, password,
				userDetailsUser.getAuthorities());
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		log.info("SecurityContextHolder에 인증 정보가 설정되었습니다. 사용자 이메일: {}", myUser.getEmail());

	}
}