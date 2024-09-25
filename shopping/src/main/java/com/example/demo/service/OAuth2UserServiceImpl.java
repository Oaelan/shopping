package com.example.demo.service;
import org.springframework.beans.factory.annotation.Autowired;
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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Map;
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
        	System.out.println(new ObjectMapper().writeValueAsString(oAuth2User.getAttributes()));
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
//        	usersEntity = new UsersEntity(name,"naver", email);
//        }
        
        if(oauthClientName.equals("Naver")) {
        	Map<String, String> responMap = (Map<String, String>) oAuth2User.getAttributes().get("response");
        	name =  responMap.get("name");
        	email =  responMap.get("email");
        	usersEntity = new UsersEntity(name,"naver",email);       	
        }
        
       
        usersRepository.save(usersEntity);
        
        
        // 5. 로그인된 사용자 정보 반환
        return new CustomOAuth2User(name);
    }
}

