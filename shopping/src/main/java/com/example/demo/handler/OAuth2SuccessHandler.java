package com.example.demo.handler;

import java.io.IOException;
import java.net.URLEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.demo.entity.CustomOAuth2User;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

// 이 클래스는 소셜 로그인 성공 시 호출되는 성공 핸들러입니다.
@Component // 이 클래스가 Spring의 빈으로 등록되도록 지정합니다.
@RequiredArgsConstructor // 모든 final 필드에 대해 생성자를 자동으로 생성합니다. (현재 사용되는 필드는 없습니다.)

public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    // 인증 성공 시 실행되는 메서드
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response,
            Authentication authentication // 인증 객체, 인증된 사용자 정보를 담고 있습니다.
    ) throws IOException, ServletException {

        // 인증된 사용자의 정보를 가져옵니다.
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        // 사용자 이름을 가져옵니다.
        String name = oAuth2User.getName();

        // 로그인 성공 후 사용자를 리디렉트하며, 쿼리 파라미터에 사용자 이름을 포함시킵니다.
        response.sendRedirect("/login/Success?name=" + URLEncoder.encode(name, "UTF-8"));
        // `URLEncoder.encode(name, "UTF-8")`을 사용하여 이름을 URL 인코딩합니다. (특수문자 처리를 위해)

    }
}
