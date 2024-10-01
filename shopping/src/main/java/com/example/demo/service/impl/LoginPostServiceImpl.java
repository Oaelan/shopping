package com.example.demo.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.UsersDTO;
import com.example.demo.entity.UsersEntity;
import com.example.demo.repository.UsersRepository;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LoginPostServiceImpl {
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

	public UsersEntity login(UsersDTO usersDTO) {
		String password = passwordEncoder.encode(usersDTO.getPassword());
		UsersEntity usersEntity = new UsersEntity();
		usersEntity.setEmail(usersDTO.getEmail());
		usersEntity.setPassword(password);
		// 데이터베이스에서 이메일로 사용자 찾기
	    Optional<UsersEntity> existingUser = usersRepository.findByEmailAndPassword(usersEntity);
	    if(existingUser.isPresent()) {
	    	return existingUser.get();
	    }
	    else {
	    	return existingUser.get();
	    }
	}

}
