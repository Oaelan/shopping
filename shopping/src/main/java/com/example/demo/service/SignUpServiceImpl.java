package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.dto.UsersDTO;
import com.example.demo.entity.UsersEntity;
import com.example.demo.repository.SignUpRepository;

import lombok.RequiredArgsConstructor;

@Service			
@RequiredArgsConstructor
public class SignUpServiceImpl implements SignUpService{

	private final SignUpRepository signUpRepository;
	
	@Override
    public int signUp(UsersDTO usersDTO) {
		UsersEntity users = UsersEntity.builder()
                .email(usersDTO.getEmail())
                .username(usersDTO.getUsername())
                .password(usersDTO.getPassword())
                .build();

        return signUpRepository.save(users).getUserId();
    }
	
}
