package com.example.demo.config;

import com.example.demo.service.*;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@EnableWebSecurity
@Configuration
@Configurable
public class SecurityConfig {

    // Spring Security 설정을 정의하는 SecurityFilterChain을 Bean으로 등록
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    http
	    	.cors(cors -> cors
	    			.configurationSource(corsConfigurationSource())
	    			)
	    	.csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화
	    	.httpBasic(httpBasic -> httpBasic.disable())
	    	.sessionManagement(sessionManagement -> sessionManagement // 소문자로 수정
	                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 관리 설정 (세션 사용 안한다는 뜻)
	            )
	        // 1. 요청에 대한 보안 설정
	        .authorizeHttpRequests(request -> request
	            	.requestMatchers("/swagger-ui/**", "/v3/api-docs/**","/signUp","/","/oauth2/**").permitAll() // antMatchers 대신 requestMatchers 사용
	                .requestMatchers("/api/v1/user/**").hasRole("USER") // USER 권한 가진 사용자만 접근 가능
	                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN") // ADMIN 권한 가진 사용자만 접근 가능
	            	// 2. 모든 요청에 대해 인증이 필요함을 설정
	                .anyRequest().authenticated()
	        )
         // 3. OAuth2 소셜 로그인 기능을 설정
	        .oauth2Login(oauth2Login -> 
            oauth2Login
                .userInfoEndpoint(userInfoEndpoint -> 
                    userInfoEndpoint.userService(oAuth2UserService())
                )
                .successHandler((request, response, authentication) -> {
                    System.out.println("인증 성공! 리디렉션 처리 중...");
                    response.sendRedirect("/loginSuccess"); // 인증 성공 시 리디렉션
                })
                .failureUrl("/loginFailure") // 인증 실패 시 리디렉션
        )
        .logout(logout -> 
            logout
                .logoutUrl("/logout") // 로그아웃 URL
                .logoutSuccessUrl("/login") // 로그아웃 성공 후 리디렉션
        );

	    // 8. 설정이 끝난 보안 필터 체인을 반환하여 적용함
	    return http.build();
	}

	@Bean
    protected CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return source; // CorsConfigurationSource로 반환됨
    }

    // OAuth2UserService Bean을 정의
    // 네이버, 구글 등의 OAuth2 프로바이더로부터 사용자 정보를 가져와 처리할 서비스
    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
        // CustomOAuth2UserService 클래스가 네이버의 사용자 정보를 처리하는 서비스로 사용됨
        return new CustomOAuth2UserService();
    }

    // 사용자 정보를 메모리에 저장하는 UserDetailsService를 Bean으로 등록
    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(
            // 새로운 사용자(user)를 생성하여 UserDetailsManager에 추가
            User.builder()
                .username("user") // 사용자 이름 설정
                .password(passwordEncoder().encode("password")) // 비밀번호 설정 (BCrypt 인코딩)
                .roles("USER") // 사용자의 역할(Role) 설정
                .build()
        );
    }

    // 비밀번호를 암호화하는 PasswordEncoder를 Bean으로 등록
    // BCryptPasswordEncoder는 비밀번호를 안전하게 암호화하기 위해 사용됨
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt 암호화 방식 사용
    }
    
}



