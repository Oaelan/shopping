package com.example.demo.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.demo.entity.RefreshTokenEntity;
import com.example.demo.repository.RefreshTokenRepository;
import com.example.demo.repository.UsersRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class JwtService {

    // @Value를 사용하여 각 필드들에 설정 파일(application.properties 또는 application.yml)의 값을 주입
    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriod;

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpirationPeriod;

    @Value("${jwt.access.header}")
    private String accessHeader;

    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private static final String EMAIL_CLAIM = "email";
    private static final String BEARER = "Bearer ";

    // UsersRepository는 사용자 정보를 관리하기 위한 레포지토리 (DB 접근)
    private final UsersRepository usersRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    /**
     * SecretKey를 생성하기 위한 Key 객체 생성
     * SecretKey를 바이트 배열로 변환한 후 Key 객체로 생성하여 서명에 사용
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    /**
     * AccessToken 생성 메소드
     * 주어진 이메일 주소를 바탕으로 JWT AccessToken을 생성
     * @param email 사용자 이메일
     * @return 생성된 AccessToken 문자열
     */
    public String createAccessToken(String email) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject("AccessToken") // JWT의 Subject를 설정 (AccessToken임을 나타냄)
                .setIssuedAt(now) // 토큰 발급 시간 설정
                .setExpiration(new Date(now.getTime() + accessTokenExpirationPeriod)) // 토큰 만료 시간 설정
                .claim(EMAIL_CLAIM, email) // 클레임으로 이메일을 포함
                .signWith(getSigningKey(), SignatureAlgorithm.HS512) // 서명 알고리즘과 서명 키 설정
                .compact(); // JWT 문자열로 변환하여 반환
    }

    /**
     * RefreshToken 생성 메소드
     * RefreshToken은 추가적인 정보를 포함하지 않음
     * @return 생성된 RefreshToken 문자열
     */
    public String createRefreshToken() {
        Date now = new Date();
        return Jwts.builder()
                .setSubject("RefreshToken") // JWT의 Subject를 설정 (RefreshToken임을 나타냄)
                .setIssuedAt(now) // 토큰 발급 시간 설정
                .setExpiration(new Date(now.getTime() + refreshTokenExpirationPeriod)) // 토큰 만료 시간 설정
                .signWith(getSigningKey(), SignatureAlgorithm.HS512) // 서명 알고리즘과 서명 키 설정
                .compact(); // JWT 문자열로 변환하여 반환
    }

    /**
     * AccessToken을 HTTP 응답 헤더에 실어서 클라이언트에게 전달
     * @param response HTTP 응답 객체
     * @param accessToken 생성된 AccessToken
     */
    public void sendAccessToken(HttpServletResponse response, String accessToken) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader(accessHeader, BEARER + accessToken);
        log.info("재발급된 Access Token : {}", accessToken);
    }

    /**
     * AccessToken과 RefreshToken을 HTTP 응답 헤더에 실어서 클라이언트에게 전달
     * @param response HTTP 응답 객체
     * @param accessToken 생성된 AccessToken
     * @param refreshToken 생성된 RefreshToken
     */
    public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken) {
        response.setStatus(HttpServletResponse.SC_OK);
        setAccessTokenHeader(response, accessToken);
        setRefreshTokenHeader(response, refreshToken);
        log.info("Access Token, Refresh Token 헤더 설정 완료");
    }

    /**
     * HTTP 요청의 헤더에서 RefreshToken을 추출
     * @param request HTTP 요청 객체
     * @return Optional로 감싼 RefreshToken 문자열 (없을 경우 빈 Optional 반환)
     */
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(refreshHeader))
                .filter(refreshToken -> refreshToken.startsWith(BEARER))
                .map(refreshToken -> refreshToken.replace(BEARER, ""));
    }

    /**
     * HTTP 요청의 헤더에서 AccessToken을 추출
     * @param request HTTP 요청 객체
     * @return Optional로 감싼 AccessToken 문자열 (없을 경우 빈 Optional 반환)
     */
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(accessHeader))
                .filter(accessToken -> accessToken.startsWith(BEARER))
                .map(accessToken -> accessToken.replace(BEARER, ""));
    }

    /**
     * AccessToken에서 이메일 정보를 추출
     * @param accessToken 액세스 토큰 문자열
     * @return Optional로 감싼 이메일 주소 (유효하지 않으면 빈 Optional 반환)
     */
    public Optional<String> extractEmail(String accessToken) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey()) // 서명 키 설정
                    .build()
                    .parseClaimsJws(accessToken) // AccessToken 검증 및 파싱
                    .getBody(); // 토큰의 본문 (클레임) 가져오기

            return Optional.ofNullable(claims.get(EMAIL_CLAIM, String.class));
        } catch (JwtException e) {
            log.error("액세스 토큰이 유효하지 않습니다.");
            return Optional.empty();
        }
    }

    /**
     * HTTP 응답 객체에 AccessToken 헤더 설정
     * @param response HTTP 응답 객체
     * @param accessToken 설정할 AccessToken
     */
    public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
        response.setHeader(accessHeader, BEARER + accessToken);
    }

    /**
     * HTTP 응답 객체에 RefreshToken 헤더 설정
     * @param response HTTP 응답 객체
     * @param refreshToken 설정할 RefreshToken
     */
    public void setRefreshTokenHeader(HttpServletResponse response, String refreshToken) {
        response.setHeader(refreshHeader, BEARER + refreshToken);
    }

    /**
     * 사용자 이메일을 기반으로 RefreshToken을 데이터베이스에 저장 또는 업데이트
     * @param email 사용자 이메일
     * @param refreshToken 저장할 RefreshToken
     */
    public void updateRefreshToken(String email, String refreshToken) {
        usersRepository.findByEmail(email)
                .ifPresentOrElse(
                        user ->{
                        	// 사용자가 있으면 해당 사용자에 대한 새로운 RefreshTokenEntity를 생성 또는 업데이트
                            RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                                    .usersEntity(user)
                                    .token(refreshToken)
                                    .createdAt(LocalDateTime.now())
                                    .expiresAt(LocalDateTime.now().plusWeeks(2)) // Refresh Token의 만료 시간을 2주 후로 설정
                                    .build();

                            refreshTokenRepository.save(refreshTokenEntity); // RefreshTokenEntity 저장
                        },
                        ()-> {
                            throw new RuntimeException("일치하는 회원이 없습니다."); // 사용자가 없으면 예외 발생
                        }
                );
    }

    /**
     * JWT 토큰의 유효성을 검사하는 메소드
     * @param token 검증할 토큰 문자열
     * @return 유효하다면 true, 그렇지 않다면 false 반환
     */
    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true; // 토큰이 유효하면 true 반환
        } catch (JwtException e) {
            log.error("유효하지 않은 토큰입니다. {}", e.getMessage());
            return false; // 토큰이 유효하지 않으면 false 반환
        }
    }
}
