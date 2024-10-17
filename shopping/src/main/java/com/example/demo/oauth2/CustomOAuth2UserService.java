package com.example.demo.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import com.example.demo.dto.UsersDTO;
import com.example.demo.entity.UsersEntity;
import com.example.demo.oauth2.CustomOAuth2User;
import com.example.demo.oauth2.cliendService.KakaoService;
import com.example.demo.oauth2.cliendService.NaverService;
import com.example.demo.repository.SignUpRepository;
import com.example.demo.repository.UsersRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.oauth2.sdk.Request;
import com.nimbusds.oauth2.sdk.Role;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Console;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	@Autowired
	private NaverService naverService;
	@Autowired
	private KakaoService kakaoService;
	
	@Override /* 사용자가 소셜 로그인 후 발급받은 액세스 토큰과 
				클라이언트(우리 서버) 정보를 user-info -url으로 요청함 */
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		// 부모의 생성자를 호출하여 리소스 서버에서 준 사용자의 정보를 OAuth2User 객체에 저장 
		OAuth2User oAuth2User = super.loadUser(userRequest);
		//userRequest에서 사용자가 소셜 로그인을 요청한 리소스 서버의 이름을 가져옴 (Naver,Kakao )
		String oauthClientName = userRequest.getClientRegistration().getClientName();

		try {
			//OAuth2 인증 후 가져온 사용자의 정보를 JSON 형식으로 로그로 확인
			
			log.info("CustomOAuth2UserService: 실행" +oauthClientName +": " + new ObjectMapper().writeValueAsString(oAuth2User.getAttributes()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		CustomOAuth2User socialOAuthUser = null;
		if(oauthClientName.equals("Naver")) {
			socialOAuthUser = naverService.extractUserInfo(oAuth2User);
		}else if(oauthClientName.equals("Kakao")) {
			socialOAuthUser = kakaoService.extractUserInfo(oAuth2User);
		}
		return socialOAuthUser;
	}
	
	
}
