package com.example.demo.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.UsersDTO;
import com.example.demo.entity.UsersEntity;
import com.example.demo.repository.UsersRepository;
import com.example.demo.service.LoginPostService;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LoginPostServiceImpl implements LoginPostService {
	@Autowired
	UsersRepository usersRepository;
	@Autowired
	PasswordEncoder passwordEncoder;

	public UsersEntity getUserInfo(String userEmail) {
		Optional<UsersEntity> existingUser = usersRepository.findByEmail(userEmail);
		UsersEntity usersEntity = null;

		if (existingUser.isPresent()) {
			return usersEntity = existingUser.get();
		} else {
			log.info("existingUser 없음" + existingUser);
			return usersEntity;
		}

	}

	public void modifyPw(UsersEntity usersEntity, String password) {
		String modifyPw = passwordEncoder.encode(password);

		// 사용자가 소셜로그인으로 가입한 회원이 아닐면서 수정할 비번과 현재 비번이 다를 경우 조건 만족
		if (usersEntity.getPassword() != modifyPw && !usersEntity.isSocialLogin()) {
			usersEntity.setPassword(modifyPw);
			usersRepository.save(usersEntity);
		} else {
			log.info("LoginPostServiceImpl.modifyPw() 오류");
		}

	}

	public UsersDTO login(UsersDTO usersDTO) {
	    // 데이터베이스에서 이메일로 사용자 찾기
	    Optional<UsersEntity> existingUser = usersRepository.findByEmail(usersDTO.getEmail());
	    
	    if (existingUser.isPresent()) {
	        UsersEntity foundUser = existingUser.get();
	        
	        // 사용자가 입력한 비밀번호와 데이터베이스의 암호화된 비밀번호 비교
	        if (passwordEncoder.matches(usersDTO.getPassword(), foundUser.getPassword())) {
	            // 비밀번호가 일치하면 UsersDTO로 변환하여 반환
	            UsersDTO loginUser = new UsersDTO();
	            loginUser.setEmail(foundUser.getEmail());
	            loginUser.setUsername(foundUser.getUsername());
	            loginUser.setUserId(foundUser.getUserId());
	            // 필요한 정보들을 더 설정할 수 있습니다.

	            return loginUser;
	        } else {
	            throw new IllegalArgumentException("Invalid password.");
	        }
	    } else {
	        throw new IllegalArgumentException("No user found with the provided email.");
	    }
	}


}
