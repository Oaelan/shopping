package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.RefreshTokenEntity;


public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Integer> {

	Optional<RefreshTokenEntity> findByToken(String refreshToken);

}
