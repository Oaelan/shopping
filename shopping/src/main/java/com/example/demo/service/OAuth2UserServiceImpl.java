package com.example.demo.service;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
@Service
@RequiredArgsConstructor
public class OAuth2UserServiceImpl extends DefaultOAuth2UserService {
	
    @Autowired
    private UsersRepository usersRepository; // 사용자 정보를 저장할 레포지토리

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String oauthClientName = userRequest.getClientRegistration().getClientName();
        
        try {
        	System.out.println("으아아악" + new ObjectMapper().writeValueAsString(oAuth2User.getAttributes()));
        }catch (Exception e) {
			e.printStackTrace();
		}
    
        
        
        UsersEntity usersEntity = null;
        String name = null;
        String email = null;
  
//   카카오 소셜 로그인 구현 경
//        if(oauthClientName.equals("naver")) {
//        	name = "naver_" + oAuth2User.getAttributes().get("name");
//        	email = "naver_" + oAuth2User.getAttributes().get("email");
//        	usersEntity = new UsersEntity(name,"naver", email)
//        }
        
        if (oauthClientName.equals("Naver")) {
            Map<String, String> responseMap = (Map<String, String>) oAuth2User.getAttributes().get("response");
            name = responseMap.get("name");
            email = responseMap.get("email");

            // 사용자 존재 여부 확인 후 저장
            Optional<UsersEntity> existingUser = usersRepository.findByEmail(email);
            if (existingUser.isPresent()) {
                usersEntity = existingUser.get();
            } else {
                usersEntity = new UsersEntity(name, "naver", email);
                usersEntity.setRole("ROLE_USER"); // 기본 역할 부여
                usersRepository.save(usersEntity);
            }
        }
        
        // 네이버 응답의 "name" 값을 최상위 속성으로 추가
        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        attributes.put("name", name);
        
        // 4. 권한 설정
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(usersEntity.getRole())); // 사용자의 역할을 권한으로 추가

        return new CustomOAuth2User(authorities, attributes, "name");

    }
}

