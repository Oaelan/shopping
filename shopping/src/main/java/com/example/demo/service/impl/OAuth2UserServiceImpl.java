package com.example.demo.service.impl;

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
import com.example.demo.entity.CustomOAuth2User;
import com.example.demo.entity.UsersEntity;
import com.example.demo.repository.SignUpRepository;
import com.example.demo.repository.UsersRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.oauth2.sdk.Request;

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
public class OAuth2UserServiceImpl extends DefaultOAuth2UserService {

	@Autowired
	private UsersRepository usersRepository; // 사용자 정보를 저장할 레포지토리

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);
		String oauthClientName = userRequest.getClientRegistration().getClientName();

		try {
			log.info("OAuth2UserServiceImpl: 실행", new ObjectMapper().writeValueAsString(oAuth2User.getAttributes()));
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 유저 엔티티 생성
		// 사용자 이름/이메일 값을 저장할 변수 선언
		UsersEntity usersEntity = null;
		String name = null;
		String email = null;


		if (oauthClientName.equals("Naver")) {
			Map<String, String> responseMap = (Map<String, String>) oAuth2User.getAttributes().get("response");
			name = responseMap.get("name");
			email = responseMap.get("email");		
			usersEntity = findOrCreateUser(name, email, "naver");
		}

		
		// 카카오 소셜 로그인 구현 경
		if(oauthClientName.equals("Kakao")) {
			log.info("카카오 id: "+oAuth2User);
			// 전체 사용자 속성 가져오기
			Map<String, Object> attributes = oAuth2User.getAttributes();
			// kakao_account 정보 가져오기
			Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
			// profile 정보 가져오기
			Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
			name = (String)profile.get("nickname"); 
			email = (String)kakaoAccount.get("email");
			log.info("카카오 nickname: "+ name + "카카오 이메일: "+email);
			 
			usersEntity = findOrCreateUser(name, email, "kakao");
		}
		
		
		
		// 네이버 응답의 "name" 값을 최상위 속성으로 추가
		Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
		attributes.put("name", name);
		attributes.put("email", email);

		// 권한 설정 - 사용자 권한 정보를 설정하는 부분입니다.
		Set<GrantedAuthority> authorities = new HashSet<>(); // 권한(역할) 정보를 저장하기 위해 Set 자료 구조를 사용해 생성합니다.

		// 사용자의 역할을 권한으로 추가합니다.
		authorities.add(new SimpleGrantedAuthority(usersEntity.getRole())); // 사용자의 역할 정보(`usersEntity.getRole()`)를 가져와서
																			// 권한 객체(`SimpleGrantedAuthority`)로 변환하고,
																			// authorities Set에 추가합니다.

		// 새로운 CustomOAuth2User 객체를 생성하여 반환합니다.
		//이 객체는 사용자 권한 정보, 사용자 속성 정보, 이름을 가져오는 키를 포함합니다.
		//CustomOAuth2User 객체는 OAuth2 인증 과정이 완료된 후 생성되며, 해당 객체는 Spring Security의 Authentication 객체 내부에 저장됩니다.
		//이 과정에서 Authentication 객체는 SecurityContextHolder를 통해 보관되고,
		// Spring Security는 이 객체를 통해 인증된 사용자 정보를 계속해서 사용할 수 있게 합니다.
		return new CustomOAuth2User(authorities, attributes, "name");

	}
	
	private UsersEntity findOrCreateUser(String name, String email, String provider) {
		// 사용자 존재 여부 확인 후 저장 Optional은 반환값이 null일 수 있을 때 이를 안전하게 다룰 수 있도록 도와주는 래퍼 클래스입니다.
		// 즉, 사용자를 찾지 못했을 때 null 대신 Optional.empty()를 반환하여 NullPointerException을 방지할 수 있게 해줍니다.
		// 이 방식으로 사용자를 찾았을 때 존재하면 UsersEntity 객체를 반환하고,
		// 없으면 비어 있는 Optional 객체를 반환합니다.
	    Optional<UsersEntity> existingUser = usersRepository.findByEmail(email);
	    if (existingUser.isPresent()) {
	    	
	        UsersEntity usersEntity = existingUser.get();
	        // 사용자 이름이 바뀐 경우에만 업데이트
	        if (usersEntity.getUsername() != name){
	            usersEntity.setUsername(name);
	            usersRepository.save(usersEntity); // 업데이트 후 저장
	        }
	        return usersEntity;
	    } else {
	        UsersEntity usersEntity = new UsersEntity(name, provider, email);
	        usersEntity.setRole("USER"); // 기본 역할 부여
	        usersRepository.save(usersEntity); // 새로운 사용자 저장
	        return usersEntity;
	    }
	}
}
