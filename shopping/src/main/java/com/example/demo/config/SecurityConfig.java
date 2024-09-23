package com.example.demo.config;
import com.example.demo.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // Spring Security 설정을 정의하는 SecurityFilterChain을 Bean으로 등록
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    http
	        // 1. 요청에 대한 보안 설정
	        .authorizeHttpRequests(authorizeRequests -> 
	            authorizeRequests
	                // 2. 모든 요청에 대해 인증이 필요함을 설정
	                .anyRequest().authenticated()
	        )
	        // 3. OAuth2 소셜 로그인 기능을 설정
	        .oauth2Login(oauth2Login -> 
	            oauth2Login
	                // 4. 로그인 성공 시 이동할 URL을 설정 (여기서는 "/loginSuccess")
	                .defaultSuccessUrl("/loginSuccess")
	                // 5. 로그인 실패 시 이동할 URL을 설정 (여기서는 "/loginFailure")
	                .failureUrl("/loginFailure")
	                // 6. 로그인 후 사용자 정보를 가져오는 방법을 설정
	                .userInfoEndpoint(userInfoEndpoint -> 
	                    // 7. CustomOAuth2UserService를 사용해 네이버에서 사용자 정보를 가져옴
	                    userInfoEndpoint.userService(oAuth2UserService())
	                )
	        );

	    // 8. 설정이 끝난 보안 필터 체인을 반환하여 적용함
	    return http.build();
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




