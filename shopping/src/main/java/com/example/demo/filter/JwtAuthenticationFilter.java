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

	private static final String NO_CHECK_URL = "/loginUser"; // "/login"으로 들어오는 요청은 Filter 작동 X

	private final JwtService jwtService;
	private final UsersRepository userRepository;

	private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		if (request.getRequestURI().equals(NO_CHECK_URL)) {
			filterChain.doFilter(request, response); // "/loginUser" 요청이 들어오면, 다음 필터 호출
			return; // return으로 이후 현재 필터 진행 막기 (안해주면 아래로 내려가서 계속 필터 진행시킴)
		}

		String refreshToken = jwtService.extractRefreshToken(request).filter(jwtService::isTokenValid).orElse(null);

		if (refreshToken != null) {
			checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
			return;
		}

		// RefreshToken이 없거나 유효하지 않다면, AccessToken을 검사하고 인증을 처리하는 로직 수행
		if (refreshToken == null) {
			checkAccessTokenAndAuthentication(request, response, filterChain);
		}
	}

	public void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
		userRepository.findByRefreshToken(refreshToken).ifPresent(user -> {
			String reIssuedRefreshToken = reIssueRefreshToken(user);
			jwtService.sendAccessAndRefreshToken(response, jwtService.createAccessToken(user.getEmail()),
					reIssuedRefreshToken);
		});
	}

	private String reIssueRefreshToken(UsersEntity user) {
		String reIssuedRefreshToken = jwtService.createRefreshToken();
		user.updateRefreshToken(reIssuedRefreshToken);
		userRepository.saveAndFlush(user);
		return reIssuedRefreshToken;
	}

	public void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {
		log.info("checkAccessTokenAndAuthentication() 호출");

		jwtService.extractAccessToken(request).filter(jwtService::isTokenValid).ifPresentOrElse(accessToken -> {
			jwtService.extractEmail(accessToken)
					.ifPresent(email -> userRepository.findByEmail(email).ifPresent(this::saveAuthentication));
			log.info("Access Token에서 사용자 이메일 추출 성공");
		}, () -> log.warn("Access Token이 유효하지 않거나 추출에 실패했습니다."));

		filterChain.doFilter(request, response);
	}

	public void saveAuthentication(UsersEntity myUser) {
		String password = myUser.getPassword();
		if (password == null) { // 소셜 로그인 유저의 비밀번호 임의로 설정 하여 소셜 로그인 유저도 인증 되도록 설정
			password = PasswordUtil.generateRandomPassword();
		}

		UserDetails userDetailsUser = org.springframework.security.core.userdetails.User.builder()
				.username(myUser.getEmail()).password(password).roles(myUser.getRole()).build();

		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetailsUser, null,
				authoritiesMapper.mapAuthorities(userDetailsUser.getAuthorities()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		log.info("SecurityContextHolder에 인증 정보가 설정되었습니다. 사용자 이메일: {}", myUser.getEmail());

	}
}
