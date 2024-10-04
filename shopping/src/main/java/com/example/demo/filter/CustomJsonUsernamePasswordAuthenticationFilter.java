package com.example.demo.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StreamUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 스프링 시큐리티의 폼 기반의 UsernamePasswordAuthenticationFilter를 참고하여 만든 커스텀 필터
 * 거의 구조가 같고, Type이 Json인 Login만 처리하도록 설정한 부분만 다르다. (커스텀 API용 필터 구현)
 * Username : 회원 아이디 -> email로 설정
 * "/login" 요청 왔을 때 JSON 값을 매핑 처리하는 필터
 */
@Slf4j
public class CustomJsonUsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final String DEFAULT_LOGIN_REQUEST_URL = "/loginUser"; // "/login"으로 오는 요청을 처리
    private static final String HTTP_METHOD = "POST"; // 로그인 HTTP 메소드는 POST
    private static final String CONTENT_TYPE = "application/json"; // JSON 타입의 데이터로 오는 로그인 요청만 처리
    private static final String USERNAME_KEY = "email"; // 회원 로그인 시 이메일 요청 JSON Key : "email"
    private static final String PASSWORD_KEY = "password"; // 회원 로그인 시 비밀번호 요청 JSON Key : "password"
    private static final AntPathRequestMatcher DEFAULT_LOGIN_PATH_REQUEST_MATCHER =
            new AntPathRequestMatcher(DEFAULT_LOGIN_REQUEST_URL, HTTP_METHOD); // "/login" + POST로 온 요청에 매칭된다.

    private final ObjectMapper objectMapper;

    // 생성자에서 로그인 요청 경로와 메서드를 설정합니다.
    public CustomJsonUsernamePasswordAuthenticationFilter(ObjectMapper objectMapper) {
        super(DEFAULT_LOGIN_PATH_REQUEST_MATCHER); // 위에서 설정한 "login" + POST로 온 요청을 처리하기 위해 설정
        this.objectMapper = objectMapper;
    }

    /**
     * 인증 처리 메소드
     * 폼 로그 요청 왔을 때 JSON 값을 매핑 처리하는 필터
     * UsernamePasswordAuthenticationFilter와 동일하게 UsernamePasswordAuthenticationToken 사용
     * StreamUtils를 통해 request에서 messageBody(JSON) 반환
     * 요청 JSON Example:
     * {
     *    "email" : "aaa@bbb.com",
     *    "password" : "test123"
     * }
     * 꺼낸 messageBody를 objectMapper.readValue()로 Map으로 변환 (Key : JSON의 키 -> email, password)
     * Map의 Key(email, password)로 해당 이메일, 패스워드 추출 후
     * UsernamePasswordAuthenticationToken의 파라미터 principal, credentials에 대입
     * AbstractAuthenticationProcessingFilter(부모)의 getAuthenticationManager()로 AuthenticationManager 객체를 반환받아
     * authenticate()의 파라미터로 UsernamePasswordAuthenticationToken 객체를 넣고 인증 처리
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {
        // Content-Type 체크를 완화하여 JSON과 x-www-form-urlencoded 모두 허용
        if (request.getContentType() == null ||
                !(request.getContentType().startsWith("application/json") || request.getContentType().startsWith("application/x-www-form-urlencoded"))) {
            // 요청의 Content-Type이 null이거나, 지원하지 않는 타입인 경우 예외 발생
            throw new AuthenticationServiceException("Authentication Content-Type not supported: " + request.getContentType());
        }

        String email;    // 사용자 이메일 변수
        String password; // 사용자 비밀번호 변수

        // 요청의 Content-Type이 JSON 형식인 경우 처리
        if (request.getContentType().startsWith("application/json")) {
            // JSON 타입 처리
            // 요청 본문을 UTF-8 문자열로 변환
            String messageBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
            
            // ObjectMapper를 사용하여 요청 본문을 Map으로 변환 (JSON 데이터를 key-value 형태로 저장)
            Map<String, String> usernamePasswordMap = objectMapper.readValue(messageBody, Map.class);
            
            // 이메일과 비밀번호 추출
            email = usernamePasswordMap.get(USERNAME_KEY);
            password = usernamePasswordMap.get(PASSWORD_KEY);
        } else {
            // Content-Type이 x-www-form-urlencoded인 경우 처리
            // 폼 데이터를 통해 전달된 요청에서 email과 password 값을 추출
            email = request.getParameter(USERNAME_KEY);
            password = request.getParameter(PASSWORD_KEY);
        }

        // 이메일이나 비밀번호가 제공되지 않은 경우 예외 발생
        if (email == null || password == null) {
            throw new AuthenticationServiceException("Username or Password not provided");
        }

        log.info("이메일" + email + "비번"+ password);
        // UsernamePasswordAuthenticationToken을 생성하여 인증 정보로 사용
        // 이 객체는 사용자 인증을 위해 사용자 정보(principal)와 자격 증명(credentials)을 포함
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(email, password);
        log.info("authRequest" + authRequest);
        // AuthenticationManager를 통해 인증 처리를 요청하고 결과 반환
        // AuthenticationManager는 Spring Security에서 실제 인증을 처리하는 컴포넌트
        return this.getAuthenticationManager().authenticate(authRequest);
    }

}
