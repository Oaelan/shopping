package com.example.demo.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.example.demo.dto.UsersDTO;
import com.example.demo.entity.UsersEntity;
import com.example.demo.repository.SignUpRepository;

import java.util.Collections;
import java.util.Map;
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private SignUpRepository signuprepository; // 사용자 정보를 저장할 레포지토리

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 1. 사용자 정보 추출
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        String name = (String) response.get("name");
        String email = (String) response.get("email");
        String id = (String) response.get("id");

        // 2. 사용자 정보를 회원가입 DTO로 변환
        UsersDTO userDTO = new UsersDTO();
        userDTO.setUsername(name);
        userDTO.setEmail(email);// (이메일이 아이디)네이버에서 받은 ID를 저장 (고유 식별자)
       

        // 3. 사용자 정보가 데이터베이스에 있는지 확인
        UsersEntity existingUser = signuprepository.findByEmail(email);
        
        if (existingUser == null) {
            // 4. 데이터베이스에 사용자가 없으면 회원가입 처리 (저장)
            UsersEntity newUser = new UsersEntity();
            newUser.setUsername(userDTO.getUsername());
            newUser.setEmail(userDTO.getEmail());
            newUser.setSocialLogin(true);
            newUser.setSocialProvider("naver");
            
            
            signuprepository.save(newUser);  // DB에 새 사용자 저장
        }

        // 5. 로그인된 사용자 정보 반환
        return new DefaultOAuth2User(
            oAuth2User.getAuthorities(),
            response,
            "id"
        );
    }
}

