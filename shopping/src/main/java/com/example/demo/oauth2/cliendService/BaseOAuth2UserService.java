package com.example.demo.oauth2.cliendService;

import java.util.Map;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.example.demo.entity.UsersEntity;
import com.example.demo.oauth2.CustomOAuth2User;
import com.example.demo.repository.UsersRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public abstract class BaseOAuth2UserService {
	protected final UsersRepository usersRepository;
	protected UsersEntity usersEntity;
	

	// 사용자 정보를 추출하는 공통 메서드 (구현은 자식 클래스에서)
	public abstract CustomOAuth2User extractUserInfo(OAuth2User oAuth2User);

	// 사용자 엔티티를 찾거나 생성하는 메서드 (공통 로직)
	public UsersEntity findOrCreateUser(String name, String email, String provider) {
		//이메일로 유저테이블에서 가입 유무 확인
		//이미 가입된 유저라면 테이블에 추가 x
		// userEntity 반
		return usersRepository.findByEmail(email).orElseGet(() -> {
			UsersEntity newUser = new UsersEntity(name, provider, email);
			newUser.setRole("USER"); // 기본 역할 부여
			return usersRepository.save(newUser);
		});
	}
}
