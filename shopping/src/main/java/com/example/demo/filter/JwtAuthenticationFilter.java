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

import com.example.demo.repository.RefreshTokenRepository;
import com.example.demo.repository.UsersRepository;
import com.example.demo.entity.RefreshTokenEntity;
import com.example.demo.entity.UsersEntity;
import com.example.demo.service.JwtService;
import com.example.demo.until.PasswordUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Jwt 인증 필터
 * "/login" 이외의 URI 요청이 왔을 때 처리하는 필터
 */
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String NO_CHECK_URL = "/loginUser"; // "/login"으로 들어오는 요청은 이 필터에서 제외됨

    // 필터에서 사용할 서비스와 레포지토리들
    private final JwtService jwtService;
    private final UsersRepository usersRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    // GrantedAuthoritiesMapper는 사용자 권한을 매핑하기 위해 사용됨
    private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    /**
    * Jwt 인증 필터
    * "/login" 이외의 URI 요청이 왔을 때 처리하는 필터 
    * 해당 클래스는 폼 로그인 후 사용자가 인증된 상태에서 요청하는 API 요청들을 처리하기 위한 역할을 합니다. 
    *
    * 기본적으로 사용자는 요청 헤더에 AccessToken만 담아서 요청
    * AccessToken 만료 시에만 RefreshToken을 요청 헤더에 AccessToken과 함께 요청
    *
    * 1. RefreshToken이 없고, AccessToken이 유효한 경우 -> 인증 성공 처리, RefreshToken을 재발급하지는 않는다.
    * 2. RefreshToken이 없고, AccessToken이 없거나 유효하지 않은 경우 -> 인증 실패 처리, 403 ERROR
    * 3. RefreshToken이 있는 경우 -> DB의 RefreshToken과 비교하여 일치하면 AccessToken 재발급, RefreshToken 재발급(RTR 방식)
    *                              인증 성공 처리는 하지 않고 실패 처리
    *
    */
    @Override
    //이 메소드안에 인증 처리/인증 실패/토큰 재발급 로직을 설정하여 필터 진입 시 인증 처리/인증 실패/토큰 재발급 등을 처리합니다.
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // "/login" 경로로 들어오는 요청은 필터를 거치지 않고 바로 다음 필터로 넘김
        if (request.getRequestURI().equals(NO_CHECK_URL)) {
            filterChain.doFilter(request, response);
            return; // return으로 이후 현재 필터 진행 막기 (안해주면 아래로 내려가서 계속 필터 진행시킴)
        }

        // 사용자 요청 헤더에서 Refresh Token 추출 및 유효성 검사
        // 사용자 요청 헤더에서 RefreshToken 추출
        // -> RefreshToken이 없거나 유효하지 않다면(DB에 저장된 RefreshToken과 다르다면) null을 반환
        // 사용자의 요청 헤더에 RefreshToken이 있는 경우는, AccessToken이 만료되어 요청한 경우밖에 없다.
        // 따라서, 위의 경우를 제외하면 추출한 refreshToken은 모두 null
        String refreshToken = jwtService.extractRefreshToken(request)
                .filter(jwtService::isTokenValid) // Refresh Token이 유효한지 확인
                .orElse(null);

        // 리프레시 토큰이 요청 헤더에 존재했다면, 사용자가 AccessToken이 만료되어서
        // RefreshToken까지 보낸 것이므로 리프레시 토큰이 DB의 리프레시 토큰과 일치하는지 판단 후,
        // 일치한다면 AccessToken을 재발급해준다.
        if (refreshToken != null) {
            checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
            return; // Refresh Token을 재발급한 후에는 인증 처리는 더 이상 필요 없음
        }

        // RefreshToken이 없거나 유효하지 않다면, AccessToken을 검사하고 인증을 처리하는 로직 수행
        // AccessToken이 없거나 유효하지 않다면, 인증 객체가 담기지 않은 상태로 다음 필터로 넘어가기 때문에 403 에러 발생
        // AccessToken이 유효하다면, 인증 객체가 담긴 상태로 다음 필터로 넘어가기 때문에 인증 성공
        if (refreshToken == null) {
            checkAccessTokenAndAuthentication(request, response, filterChain);
        }
    }

    /**
     *  [리프레시 토큰으로 유저 정보 찾기 & 액세스 토큰/리프레시 토큰 재발급 메소드]
     *  파라미터로 들어온 헤더에서 추출한 리프레시 토큰으로 DB에서 유저를 찾고, 해당 유저가 있다면
     *  JwtService.createAccessToken()으로 AccessToken 생성,
     *  reIssueRefreshToken()로 리프레시 토큰 재발급 & DB에 리프레시 토큰 업데이트 메소드 호출
     *  그 후 JwtService.sendAccessTokenAndRefreshToken()으로 응답 헤더에 보내기
     */
    private void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
        // RefreshTokenEntity를 데이터베이스에서 찾음
        Optional<RefreshTokenEntity> refreshTokenEntityOpt = refreshTokenRepository.findByToken(refreshToken);

        refreshTokenEntityOpt.ifPresent(refreshTokenEntity -> {
            UsersEntity user = refreshTokenEntity.getUsersEntity(); // 리프레시 토큰을 가진 사용자 찾기
            String reIssuedRefreshToken = reIssueRefreshToken(refreshTokenEntity); // Refresh Token 재발급
            // 새로운 Access Token과 Refresh Token을 응답 헤더에 추가
            jwtService.sendAccessAndRefreshToken(response, jwtService.createAccessToken(user.getEmail()), reIssuedRefreshToken);
        });
    }

    /**
     * [리프레시 토큰 재발급 & DB에 리프레시 토큰 업데이트 메소드]
     * jwtService.createRefreshToken()으로 리프레시 토큰 재발급 후
     * DB에 재발급한 리프레시 토큰 업데이트 후 Flush
     */
    private String reIssueRefreshToken(RefreshTokenEntity refreshTokenEntity) {
        // 새 리프레시 토큰 생성
        String reIssuedRefreshToken = jwtService.createRefreshToken();
        // 생성된 리프레시 토큰으로 기존 엔티티 업데이트
        refreshTokenEntity.setToken(reIssuedRefreshToken);
        refreshTokenEntity.setCreatedAt(LocalDateTime.now());
        refreshTokenEntity.setExpiresAt(LocalDateTime.now().plusWeeks(2)); // 만료 시간 2주 후로 설정
        refreshTokenRepository.save(refreshTokenEntity); // 갱신된 리프레시 토큰을 DB에 저장
        return reIssuedRefreshToken; // 새로 발급된 리프레시 토큰 반환
    }

    /**
     * [액세스 토큰 체크 & 인증 처리 메소드]
     * request에서 extractAccessToken()으로 액세스 토큰 추출 후, isTokenValid()로 유효한 토큰인지 검증
     * 유효한 토큰이면, 액세스 토큰에서 extractEmail로 Email을 추출한 후 findByEmail()로 해당 이메일을 사용하는 유저 객체 반환
     * 그 유저 객체를 saveAuthentication()으로 인증 처리하여
     * 인증 허가 처리된 객체를 SecurityContextHolder에 담기
     * 그 후 다음 인증 필터로 진행
     */
    private void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response,
                                                   FilterChain filterChain) throws ServletException, IOException {
        log.info("checkAccessTokenAndAuthentication() 호출");
        jwtService.extractAccessToken(request) // Access Token 추출
                .filter(jwtService::isTokenValid) // Access Token이 유효한지 검증
                .ifPresent(accessToken -> jwtService.extractEmail(accessToken) // 유효하다면 이메일 추출
                        .ifPresent(email -> usersRepository.findByEmail(email) // 이메일로 사용자 찾기
                                .ifPresent(this::saveAuthentication))); // 인증 정보 저장

        filterChain.doFilter(request, response); // 다음 필터로 요청 전달
    }

    /**
     * [인증 허가 메소드]
     * 파라미터의 유저 : 우리가 만든 회원 객체 / 빌더의 유저 : UserDetails의 User 객체
     *
     * new UsernamePasswordAuthenticationToken()로 인증 객체인 Authentication 객체 생성
     * UsernamePasswordAuthenticationToken의 파라미터
     * 1. 위에서 만든 UserDetailsUser 객체 (유저 정보)
     * 2. credential(보통 비밀번호로, 인증 시에는 보통 null로 제거)
     * 3. Collection < ? extends GrantedAuthority>로,
     * UserDetails의 User 객체 안에 Set<GrantedAuthority> authorities이 있어서 getter로 호출한 후에,
     * new NullAuthoritiesMapper()로 GrantedAuthoritiesMapper 객체를 생성하고 mapAuthorities()에 담기
     *
     * SecurityContextHolder.getContext()로 SecurityContext를 꺼낸 후,
     * setAuthentication()을 이용하여 위에서 만든 Authentication 객체에 대한 인증 허가 처리
     */
    private void saveAuthentication(UsersEntity userEntity) {
        // 소셜 로그인 사용자는 비밀번호가 없을 수 있으므로, 랜덤 비밀번호 설정
        String password = userEntity.getPassword();
        if (password == null) {
            password = PasswordUtil.generateRandomPassword(); // 비밀번호 생성
        }

        // UserDetails 객체 생성, Spring Security에서 사용
        UserDetails userDetailsUser = org.springframework.security.core.userdetails.User.builder()
                .username(userEntity.getEmail()) // 사용자 이메일 설정
                .password(password) // 사용자 비밀번호 설정
                .roles(userEntity.getRole()) // 사용자 역할 설정 (예: ROLE_USER)
                .build();

        // 인증 객체 생성, 비밀번호는 null로 설정 (추후 인증 단계에서 필요 없기 때문)
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetailsUser,
                null,
                authoritiesMapper.mapAuthorities(userDetailsUser.getAuthorities()) // 사용자 권한 매핑
        );

        // SecurityContextHolder에 인증 객체 저장, 이를 통해 Spring Security에서 해당 사용자가 인증되었다고 인식
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
