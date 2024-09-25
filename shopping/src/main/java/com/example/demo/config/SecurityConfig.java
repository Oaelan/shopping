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
	
    private final DefaultOAuth2UserService oAuthUserService;
	private final OAuth2SuccessHandler oAuth2SuccessHandler;
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
	        		.dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
	        		.requestMatchers("/swagger-ui/**", "/v3/api-docs/**","/signUp","/","/login/oauth2/code/**").permitAll() // antMatchers 대신 requestMatchers 사용
	                .requestMatchers("/api/v1/user/**").hasRole("USER") // USER 권한 가진 사용자만 접근 가능
	                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN") // ADMIN 권한 가진 사용자만 접근 가능
	            	// 2. 모든 요청에 대해 인증이 필요함을 설정
	                .anyRequest().authenticated()
	        )
         // 3. OAuth2 소셜 로그인 기능을 설정
	        .oauth2Login(oauth2 ->oauth2
	        		//http://localhost:8080//api/v1/auth/oauth2/naver 인증 요청 주소 커스텀
	        		.authorizationEndpoint(endpoint -> endpoint.baseUri("/api/v1/auth/oauth2"))
	                .redirectionEndpoint(endpoint -> endpoint.baseUri("/login/oauth2/code/*")) // 여기에서 설정
	        		.userInfoEndpoint(endpoint -> endpoint.userService(oAuthUserService))
	        		.successHandler(oAuth2SuccessHandler)
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

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
        											   
				        		"/swagger-ui/**", 
				        		"/v3/api-docs/**",
				        		"/signUp",
				        		"/",
				        		"/login/oauth2/code/**"
				        											    
        											   );
    }

    
    // 비밀번호를 암호화하는 PasswordEncoder를 Bean으로 등록
    // BCryptPasswordEncoder는 비밀번호를 안전하게 암호화하기 위해 사용됨
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt 암호화 방식 사용
    }
    
}



