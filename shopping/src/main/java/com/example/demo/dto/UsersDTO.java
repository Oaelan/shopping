package com.example.demo.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsersDTO {
	
	private int userId;
    private String username;
    private String password;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isSocialLogin;
    private String socialProvider;
}
