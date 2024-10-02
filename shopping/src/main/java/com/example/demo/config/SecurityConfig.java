package com.example.demo.config;

import com.example.demo.handler.OAuth2SuccessHandler;
import com.example.demo.service.*;

import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
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
@RequiredArgsConstructor
public class SecurityConfig {
	
    private final DefaultOAuth2UserService oAuthUserService; // 인증후 토큰을 받아 객체 생성
	private final OAuth2SuccessHandler oAuth2SuccessHandler;
	
	
    // Spring Security 설정을 정의하는 SecurityFilterChain을 Bean으로 등록
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    http
	        // CORS 설정
	        .cors(cors -> cors
	            .configurationSource(corsConfigurationSource())
	        )
	        // CSRF 보호 비활성화
	        .csrf(csrf -> csrf.disable())
	        // HTTP 기본 인증 비활성화
	        .httpBasic(httpBasic -> httpBasic.disable())
	        // 세션 관리 설정 (세션 사용, 항상 새로운 세션 생성)
	        .sessionManagement(sessionManagement -> sessionManagement
	            .sessionCreationPolicy(SessionCreationPolicy.ALWAYS) // 세션 정책 설정: ALWAYS는 매번 새로운 세션을 생성함
	        )
	        // 1. 요청에 대한 보안 설정
	        .authorizeHttpRequests(request -> request
	            // FORWARD 타입의 요청은 누구나 접근 가능하게 설정
	            .dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
	            // Swagger UI, 회원가입, 로그인 등 특정 경로는 모두 접근 가능하게 설정
	            .requestMatchers("/swagger-ui/**", 
	            				"/v3/api-docs/**", 
	            				"/signUp", "/", 
	            				"/login/oauth2/code/**", 
	            				"/login", 
	            				"/login/Success/api/test",
	            				"/oauth2/authorization/**", 
	            				"/api/v1/auth/oauth2/**",
	            				"/Failure"
	            				).permitAll()
	            // `/login/Success` 경로는 "USER" 권한을 가진 사용자만 접근 가능
	            .requestMatchers("/user/login/**").hasRole("USER")
	            // `/api/v1/admin/**` 경로는 "ADMIN" 권한을 가진 사용자만 접근 가능
	            .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
	            // 그 외 모든 요청에 대해 인증 필요 설정
	            .anyRequest().authenticated()
	        )
	        // 3. OAuth2 소셜 로그인 기능을 설정
	        .oauth2Login(oauth2 -> oauth2
	            // 소셜 로그인 요청 주소를 커스텀 (`/api/v1/auth/oauth2/naver` 같은 형식)
	        	// 네이버 소셜 로그인 버튼의 a링크 주소 (기본형은 /oauth2/authorization/{provider})
	            .authorizationEndpoint(endpoint -> endpoint.baseUri("/api/v1/auth/oauth2"))
	            // 리디렉션 엔드포인트를 커스텀 (`/login/oauth2/code/*`)
	            .redirectionEndpoint(endpoint -> endpoint.baseUri("/login/oauth2/code/*"))
	            // OAuth2 사용자 서비스 설정 (사용자 정보를 가져올 서비스)
	            .userInfoEndpoint(endpoint -> endpoint.userService(oAuthUserService))
	            // 인증 성공 후 핸들러 설정 (성공 시 실행되는 로직 정의)
	            .successHandler(oAuth2SuccessHandler)
	        );

	    return http.build(); //http.build()를 호출하여 최종 보안 설정을 빌드하고,이 설정된 보안 필터 체인을 반환하는 것입니다.
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
//
//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return (web) -> web.ignoring().requestMatchers(
//        											   
//				        		"/swagger-ui/**", 
//				        		"/v3/api-docs/**",
//				        		"/signUp",
//				        		"/",
//				        		"/login/oauth2/code/**"
//				        											    
//        											   );
//    } 
 //https://velog.io/@kgb/spring-security-permit-all-%EB%AC%B4%EC%8B%9C%EC%95%88%EB%90%98%EB%8A%94-%EA%B2%BD%EC%9A%B0

    
    // 비밀번호를 암호화하는 PasswordEncoder를 Bean으로 등록
    // BCryptPasswordEncoder는 비밀번호를 안전하게 암호화하기 위해 사용됨
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt 암호화 방식 사용
    }
    
}



