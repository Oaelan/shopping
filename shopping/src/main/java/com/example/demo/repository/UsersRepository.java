package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.UsersEntity;

public interface UsersRepository extends JpaRepository<UsersEntity, Integer> {

	// 이메일로 사용자를 찾는 메서드 선언
	Optional<UsersEntity> findByEmail(String email);
	Optional<UsersEntity> findByRefreshToken(String refreshToken);
	
}