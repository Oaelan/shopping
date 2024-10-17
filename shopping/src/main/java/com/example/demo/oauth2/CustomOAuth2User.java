package com.example.demo.oauth2;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.nimbusds.oauth2.sdk.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Getter
@Slf4j
public class CustomOAuth2User extends DefaultOAuth2User {

	private String email;
	private String name;
	
	// Constructor that matches the signature required
	public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes,
			String nameAttributeKey, String email, String name) {
		super(authorities, attributes, nameAttributeKey);
		this.email = email;
		this.name = name;
		
	}
	

}
