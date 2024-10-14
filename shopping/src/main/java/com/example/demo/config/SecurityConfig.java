package com.example.demo.config;

import com.example.demo.filter.CustomJsonUsernamePasswordAuthenticationFilter;
import com.example.demo.filter.CustomLogoutFilter;
import com.example.demo.filter.JwtAuthenticationFilter;
import com.example.demo.handler.*;
import com.example.demo.handler.OAuth2SuccessHandler;
import com.example.demo.repository.UsersRepository;
import com.example.demo.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;


import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity //@EnableWebSecurity 어노테이션을 붙여야 Spring Security 기능을 사용할 수 있습니다!
@Configuration
@Configurable
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

	private final DefaultOAuth2UserService oAuthUserService; // 인증후 토큰을 받아 객체 생성
	private final OAuth2SuccessHandler oAuth2SuccessHandler;
	private final LoginService loginService;
	private final JwtService jwtService;
	private final UsersRepository userRepository;
	private final ObjectMapper objectMapper;
	private final JwtLogoutSuccessHandler logoutSuccessHandler;


	// Spring Security 설정을 정의하는 SecurityFilterChain을 Bean으로 등록

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http	.formLogin(form -> form.disable()) // FormLogin 비활성화
				.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable())) // X-Frame-Options 비활성화
				.httpBasic(httpBasic -> httpBasic.disable())
				// CORS 설정
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				// CSRF 보호 비활성화
				.csrf(csrf -> csrf.disable())
				// HTTP 기본 인증 비활성화				
				.httpBasic(httpBasic -> httpBasic.disable())
				
				// 세션 관리 설정 (세션 사용, 항상 새로운 세션 생성)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				// 1. 요청에 대한 보안 설정
				.authorizeHttpRequests(request -> request
						// FORWARD 타입의 요청은 누구나 접근 가능하게 설정
						.dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
						// Swagger UI, 회원가입, 로그인 등 특정 경로는 모두 접근 가능하게 설정
						.requestMatchers("/swagger-ui/**", 
								"/v3/api-docs/**", "/signUp", 
								"/", "/login/oauth2/code/**",
								"/login","/favicon.ico",
								"/oauth2/authorization/**",
								"/api/v1/auth/oauth2/**",
								"/Failure","/loginUser","/user/login/**")
						.permitAll()
						// `/login/Success` 경로는 "USER" 권한을 가진 사용자만 접근 가능
						.requestMatchers("/api/user/login/**").hasRole("USER")
						// `/api/v1/admin/**` 경로는 "ADMIN" 권한을 가진 사용자만 접근 가능
						.requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
						// 그 외 모든 요청에 대해 인증 필요 설정

						.anyRequest().authenticated()

				)
				// 3. OAuth2 소셜 로그인 기능을 설정
				.oauth2Login(oauth2 -> oauth2
						.loginPage("/oauth2-login")
						
						// 소셜 로그인 요청 주소를 커스텀 (`/api/v1/auth/oauth2/naver` 같은 형식)
						// 네이버 소셜 로그인 버튼의 a링크 주소 (기본형은 /oauth2/authorization/{provider})
						.authorizationEndpoint(endpoint -> endpoint.baseUri("/api/v1/auth/oauth2"))
						// 리디렉션 엔드포인트를 커스텀 (`/login/oauth2/code/*`)
						.redirectionEndpoint(endpoint -> endpoint.baseUri("/login/oauth2/code/*"))
						// OAuth2 사용자 서비스 설정 (사용자 정보를 가져올 서비스)
						.userInfoEndpoint(endpoint -> endpoint.userService(oAuthUserService))
						// 인증 성공 후 핸들러 설정 (성공 시 실행되는 로직 정의)
						.successHandler(oAuth2SuccessHandler)
				)
				
		.logout(logout -> logout
	            .logoutUrl("/logout") // 기본 로그아웃 URL 설정
	            .logoutSuccessUrl("/login") // 로그아웃 성공 후 리다이렉트 URL
	            .addLogoutHandler(new LogoutHandler() { // 커스텀 로그아웃 핸들러 추가
	            	 public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
	            	    
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
	            	 
	            })
	            .logoutSuccessHandler(new LogoutSuccessHandler() { // 커스텀 로그아웃 성공 핸들러 추가
	                @Override
	                public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
	                    try {
							response.sendRedirect("/login");
						} catch (java.io.IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} // 로그아웃 성공 후 리다이렉트
	                }
	            })
	            .deleteCookies("JSESSIONID") // 쿠키 삭제
	        );

			
		// 원래 스프링 시큐리티 필터 순서가 LogoutFilter 이후에 로그인 필터 동작
        // 따라서, LogoutFilter 이후에 우리가 만든 필터 동작하도록 설정
        // 순서 : LogoutFilter -> JwtAuthenticationProcessingFilter -> CustomJsonUsernamePasswordAuthenticationFilter
        http.addFilterAfter(customJsonUsernamePasswordAuthenticationFilter(), LogoutFilter.class);
        http.addFilterBefore(jwtAuthenticationProcessingFilter(), CustomJsonUsernamePasswordAuthenticationFilter.class);
		
		return http.build(); // http.build()를 호출하여 최종 보안 설정을 빌드하고,이 설정된 보안 필터 체인을 반환하는 것입니다.
	}

	@Bean
	protected CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.addAllowedOrigin("http://localhost:8080");
		corsConfiguration.addAllowedMethod("*");
		corsConfiguration.addAllowedHeader("*");

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfiguration);
		
//		// 클라이언트에 노출할 헤더 설정
//	    corsConfiguration.addExposedHeader("Authorization");
//	    corsConfiguration.addExposedHeader("Authorization-refresh");

		return source; // CorsConfigurationSource로 반환됨
	}

	// OAuth2UserService Bean을 정의
	// 네이버, 구글 등의 OAuth2 프로바이더로부터 사용자 정보를 가져와 처리할 서비스

	// 사용자 정보를 메모리에 저장하는 UserDetailsService를 Bean으로 등록
	@Bean
	public UserDetailsService userDetailsService() {
		return new InMemoryUserDetailsManager(
				// 새로운 사용자(user)를 생성하여 UserDetailsManager에 추가
				User.builder().username("user") // 사용자 이름 설정
						.password(passwordEncoder().encode("password")) // 비밀번호 설정 (BCrypt 인코딩)
						.roles("USER") // 사용자의 역할(Role) 설정
						.build());
	}

//	@Bean
//	public WebSecurityCustomizer webSecurityCustomizer() {
//	    // WebSecurityCustomizer를 통해 "/favicon.ico" 경로를 보안 필터 체인에서 제외합니다.
//	    return (web) -> web.ignoring().requestMatchers("/favicon.ico");
//	}

	// https://velog.io/@kgb/spring-security-permit-all-%EB%AC%B4%EC%8B%9C%EC%95%88%EB%90%98%EB%8A%94-%EA%B2%BD%EC%9A%B0

	// 비밀번호를 암호화하는 PasswordEncoder를 Bean으로 등록
	// BCryptPasswordEncoder는 비밀번호를 안전하게 암호화하기 위해 사용됨
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(); // BCrypt 암호화 방식 사용
	}
	 /**
     * AuthenticationManager 설정 후 등록
     * PasswordEncoder를 사용하는 AuthenticationProvider 지정 (PasswordEncoder는 위에서 등록한 PasswordEncoder 사용)
     * FormLogin(기존 스프링 시큐리티 로그인)과 동일하게 DaoAuthenticationProvider 사용
     * UserDetailsService는 커스텀 LoginService로 등록
     * 또한, FormLogin과 동일하게 AuthenticationManager로는 구현체인 ProviderManager 사용(return ProviderManager)
     *
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(loginService);
        log.info("AuthenticationManager" + provider);
        return new ProviderManager(provider);
    }

	/**
     * 로그인 성공 시 호출되는 LoginSuccessJWTProviderHandler 빈 등록
     */
    @Bean
    public LoginSuccessHandler loginSuccessHandler() {
        return new LoginSuccessHandler(jwtService, userRepository);
    }

    /**
     * 로그인 실패 시 호출되는 LoginFailureHandler 빈 등록
     */
    @Bean
    public LoginFailureHandler loginFailureHandler() {
        return new LoginFailureHandler();
    }
    /**
     * CustomJsonUsernamePasswordAuthenticationFilter 빈 등록
     * 커스텀 필터를 사용하기 위해 만든 커스텀 필터를 Bean으로 등록
     * setAuthenticationManager(authenticationManager())로 위에서 등록한 AuthenticationManager(ProviderManager) 설정
     * 로그인 성공 시 호출할 handler, 실패 시 호출할 handler로 위에서 등록한 handler 설정
     */
    @Bean
    public CustomJsonUsernamePasswordAuthenticationFilter customJsonUsernamePasswordAuthenticationFilter() {
        CustomJsonUsernamePasswordAuthenticationFilter customJsonUsernamePasswordLoginFilter
                = new CustomJsonUsernamePasswordAuthenticationFilter(objectMapper);
        customJsonUsernamePasswordLoginFilter.setAuthenticationManager(authenticationManager());
        customJsonUsernamePasswordLoginFilter.setAuthenticationSuccessHandler(loginSuccessHandler());
        customJsonUsernamePasswordLoginFilter.setAuthenticationFailureHandler(loginFailureHandler());
        return customJsonUsernamePasswordLoginFilter;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationProcessingFilter() {
    	JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtService, userRepository);
        return jwtAuthenticationFilter;
    }
    

}
