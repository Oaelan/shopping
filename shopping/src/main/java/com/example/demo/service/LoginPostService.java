package com.example.demo.service;

import com.example.demo.entity.UsersEntity;

public interface LoginPostService {
	public UsersEntity getUserInfo(String userEmail);
	public void modifyPw(String modifyPw);
	public UsersEntity login(String email,String password);

}
