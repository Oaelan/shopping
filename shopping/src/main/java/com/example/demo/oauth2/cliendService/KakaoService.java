package com.example.demo.oauth2.cliendService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.example.demo.oauth2.CustomOAuth2User;
import com.example.demo.repository.UsersRepository;

import lombok.extern.slf4j.Slf4j;
@Service
@Slf4j
public class KakaoService extends BaseOAuth2UserService {

    public KakaoService(UsersRepository usersRepository) {
        super(usersRepository);
    }
    
    @Override
    public CustomOAuth2User extractUserInfo(OAuth2User oAuth2User) {
        Map<String, Object> response = (Map<String, Object>) oAuth2User.getAttributes().get("kakao_account");
        Map<String, Object> responseName = (Map<String, Object>) response.get("profile");
        
        String email = (String) response.get("email");
        String name = (String) responseName.get("nickname");
       
        //log.info("name" + name + "email" + email);
        // 사용	
        usersEntity = findOrCreateUser(name, email, "kakao");
        String nameAttributeKey = "id";
        Map<String, Object> attributes = new HashMap<>(response);
        attributes.put("id", nameAttributeKey);
		Set<GrantedAuthority> authorities = new HashSet<>();
		authorities.add(new SimpleGrantedAuthority(usersEntity.getRole())); // 사용자의 역할 정보(`usersEntity.getRole()`)를 가져와서												// 권한 객체(`SimpleGrantedAuthority`)로 변환하고,																				// authorities Set에 추가합니다.																						

		return new CustomOAuth2User(authorities, attributes,nameAttributeKey, email, name);

    }
}
