package com.example.demo.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.UsersDTO;
import com.example.demo.entity.UsersEntity;
import com.example.demo.repository.SignUpRepository;

import lombok.RequiredArgsConstructor;

@Service			
@RequiredArgsConstructor
public class SignUpServiceImpl implements SignUpService{

	private final SignUpRepository signUpRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public int signUp(UsersDTO usersDTO) {
		// 비밀번호 암호화
	    String encodedPassword = passwordEncoder.encode(usersDTO.getPassword());
	
	    // 암호화된 비밀번호로 사용자 엔티티 생성
	    UsersEntity users = UsersEntity.builder()
	            .email(usersDTO.getEmail())
	            .username(usersDTO.getUsername())
	            .password(encodedPassword)  // 암호화된 비밀번호 저장
	            .isSocialLogin(false)
	            .role("ROLE_USER")
	            .build();
	
	    // 사용자 저장 후 userId 반환
	    return signUpRepository.save(users).getUserId();
    }
	
}
