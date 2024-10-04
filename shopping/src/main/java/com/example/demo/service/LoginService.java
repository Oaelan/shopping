package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.entity.UsersEntity;
import com.example.demo.repository.UsersRepository;
@Service
public class LoginService implements UserDetailsService {
	@Autowired
	UsersRepository usersRepository;
	
	@Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UsersEntity usersEntity = usersRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일이 존재하지 않습니다."));

        return org.springframework.security.core.userdetails.User.builder()
                .username(usersEntity.getEmail())
                .password(usersEntity.getPassword())
                .roles(usersEntity.getRole())
                .build();
    }
}
