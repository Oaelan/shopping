package com.example.demo.jwt;

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
import com.example.demo.until.PasswordUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

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
	private static final String CHECK_URL = "/api/user/login/";
	private static final String SOCIAL_CHECK_URL = "/api/user/login/socialLogined"; 
	private final JwtService jwtService;
	private final UsersRepository userRepository;

	//private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
	        throws ServletException, IOException {

	    // 특정 URL 체크
	    if (request.getRequestURI().startsWith(CHECK_URL)) {
		    log.info("요청 URI: {}", request.getRequestURI());
	        // 액세스 토큰을 확인 (헤더에서 추출)
	        String accessToken = jwtService.extractAccessToken(request).filter(jwtService::isTokenValid).orElse(null);
	        
	        if (accessToken == null) {
	            // 액세스 토큰이 없거나 유효하지 않은 경우
	            log.info("액세스 토큰이 유효하지 않음. 리프레시 토큰 검사 시작.");

	            // 리프레시 토큰을 쿠키에서 추출
	            String refreshToken = jwtService.extractRefreshToken(request).filter(jwtService::isTokenValid).orElse(null);
	            
	            if (refreshToken != null) {
	                // 리프레시 토큰이 유효하면 액세스/리프레시 토큰 재발급
	                checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
	                return; // 필터 체인 진행 중단, 새로운 액세스 토큰 발급 후 응답
	            } else {
	                // 리프레시 토큰도 없거나 유효하지 않은 경우
	                log.info("리프레시 토큰이 유효하지 않음.");
	                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "리프레시 토큰이 유효하지 않습니다.");
	                return;
	            }
	        }

	        // 액세스 토큰이 유효한 경우
	        log.info("액세스 토큰이 유효함.");
	        checkAccessTokenAndAuthentication(request, response, filterChain);	    	    	
	    }else if(request.getRequestURI().equals(SOCIAL_CHECK_URL)){
	    	log.info("소셜로그인 후 발급된 레프리쉬 토큰으로 액세스 토큰 발급");
	        // 액세스 토큰을 확인 (헤더에서 추출)
	        String UserAccessToken = jwtService.extractAccessToken(request).filter(jwtService::isTokenValid).orElse(null);
	        String UserRefreshToken = jwtService.extractRefreshToken(request).filter(jwtService::isTokenValid).orElse(null);
	    	Optional<UsersEntity> user = userRepository.findByRefreshToken(UserRefreshToken);
	        String email = user.get().getEmail();
	        
	        // 소셜 로그인 경우 리프레쉬 토큰의 값만 들어있음 
	        if(UserAccessToken == null && UserRefreshToken != null) {
	        	String accessToken = jwtService.createAccessToken(email);
	        	jwtService.sendAccessToken(response, accessToken);
	        }
	    	
	        checkAccessTokenAndAuthentication(request, response, filterChain);	    	    	

	    }else {
	    	// 지정된 url 경로로 요청이 들어오지 않았을 때
	        log.info("JwtAuthenticationFilter 건너뜀.");
	        filterChain.doFilter(request, response); // 해당 URL이 아니면 필터를 건너뜀
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