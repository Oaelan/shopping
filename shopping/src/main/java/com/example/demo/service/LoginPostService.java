package com.example.demo.service;
import com.example.demo.dto.UsersDTO;
import com.example.demo.entity.UsersEntity;

public interface LoginPostService {
	public UsersEntity getUserInfo(String userEmail);
	public void modifyPw(UsersEntity usersEntity,String modifyPw);
	public UsersDTO login(UsersDTO usersDTO);
}
