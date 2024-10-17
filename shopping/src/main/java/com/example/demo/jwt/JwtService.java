package com.example.demo.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.demo.repository.UsersRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class JwtService {

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.access.expiration}")
    private int accessTokenExpirationPeriod;

    @Value("${jwt.refresh.expiration}")
    private int refreshTokenExpirationPeriod;

    @Value("${jwt.access.header}")
    private String accessHeader;

    @Value("${jwt.refresh.cookie}")
    private String refreshCookie;

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String EMAIL_CLAIM = "email";
    private static final String BEARER = "Bearer ";

    private final UsersRepository userRepository;

    /**
     * AccessToken 생성 메소드
     * jwt 유효기간과 / cookie 유효기간은 다름 !
     * jwt의 유효기간은 jwt가 유효한 기간
     * cookie 유효기간은 클라이언트 측에서 쿠키가 남아있는 기간 (지나면 삭제)
     */
    public String createAccessToken(String email) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(ACCESS_TOKEN_SUBJECT)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenExpirationPeriod))
                .claim(EMAIL_CLAIM, email)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * RefreshToken 생성 메소드
     */
    public String createRefreshToken() {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(REFRESH_TOKEN_SUBJECT)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenExpirationPeriod))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * AccessToken을 응답 헤더에 추가
     */
    public void sendAccessToken(HttpServletResponse response, String accessToken) {
        response.setStatus(HttpServletResponse.SC_OK);
        setAccessTokenHeader(response, accessToken);
        log.info("재발급된 Access Token : {}", accessToken);
    }

    /**
     * AccessToken 및 RefreshToken을 응답 헤더에 추가
     */
    public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken) {
        response.setStatus(HttpServletResponse.SC_OK);
        setAccessTokenHeader(response, accessToken);
        setRefreshTokenCookie(response, refreshToken);    
        log.info("Access Token, Refresh Token 헤더 설정 완료");
    }

    /**
     * 헤더에서 RefreshToken 추출
     */
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        // 모든 쿠키를 가져옴
        Cookie[] cookies = request.getCookies();
        
        // 쿠키가 없는 경우 Optional.empty() 반환
        if (cookies == null) {
            log.debug("리프레쉬 쿠키가 없습니다.");
            return Optional.empty();
        }
        
        // 지정한 쿠키(refreshHeader)를 찾음
        return Arrays.stream(cookies)
                     .filter(cookie -> cookie.getName().equals(refreshCookie))  // refreshHeader 이름의 쿠키를 찾음
                     .map(Cookie::getValue)  // 쿠키의 값을 가져옴
                     .findFirst();
    }


    /**
     * 헤더에서 AccessToken 추출
     */
    public Optional<String> extractAccessToken(HttpServletRequest request) {
    	 // "accessHeader" 헤더에서 값을 가져옵니다.
        String authorizationHeader = request.getHeader(accessHeader);

        // 헤더가 없거나 형식이 잘못된 경우 처리
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER)) {
            log.warn("Authorization 헤더가 없거나 Bearer 형식이 아닙니다.");
            return Optional.empty();
        }

        // "Bearer " 접두어 제거 후 토큰 추출
        String accessToken = authorizationHeader.substring(7);
        log.info("헤더에서 추출한 Access Token: {}", accessToken);

        return Optional.of(accessToken);
    }

    /**
     * AccessToken에서 Email 추출
     */
    public Optional<String> extractEmail(String accessToken) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
            return Optional.ofNullable(claims.get(EMAIL_CLAIM, String.class));
        } catch (Exception e) {
            log.error("액세스 토큰이 유효하지 않습니다. {}", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * AccessToken 헤더 설정
     */
    public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
    	response.setHeader(accessHeader, accessToken);
        log.info("Access Token 헤더 설정 완료");
    }

    /**
     * RefreshToken 헤더 설정
     */
    public void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
    	// 리프레시 토큰을 쿠키에 설정
        Cookie cookie = new Cookie(refreshCookie, refreshToken);
        cookie.setHttpOnly(true); // 자바스크립트에서 접근 불가능
        cookie.setSecure(false); // HTTPS에서만 전송(true)  로컬 개발 환경에서는 false
        cookie.setPath("/"); // 쿠키의 유효 경로 설정
        cookie.setMaxAge(refreshTokenExpirationPeriod/1000); // 초 단위,쿠키 만료 시간 설정 (예: 7일)

        response.addCookie(cookie); // 쿠키 추가
        log.info("Refresh Token 쿠키 설정 완료");
    }

    /**
     * RefreshToken DB 저장(업데이트)
     */
    public void updateRefreshToken(String email, String refreshToken) {
        userRepository.findByEmail(email)
                .ifPresentOrElse(
                        user -> user.updateRefreshToken(refreshToken),
                        () -> {
                            throw new IllegalArgumentException("일치하는 회원이 없습니다.");
                        }
                );
    }

    /**
     * 토큰의 유효성 검사
     */
    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("유효하지 않은 토큰입니다. {}", e.getMessage());
            return false;
        }
    }

    /**
     * SecretKey를 기반으로 HMAC 키 생성
     */
    private Key getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS512.getJcaName());
    }
}
