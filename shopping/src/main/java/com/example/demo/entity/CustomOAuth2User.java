package com.example.demo.entity;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CustomOAuth2User implements OAuth2User {
    private Set<GrantedAuthority> authorities;
    private Map<String, Object> attributes;
    private String nameAttributeKey;

    // Constructor that matches the signature required
    public CustomOAuth2User(Set<GrantedAuthority> authorities, Map<String, Object> attributes,
                            String nameAttributeKey) {
        this.authorities = authorities;
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;  // 사용자의 속성을 반환합니다.
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;  // 사용자의 권한을 반환합니다.
    }

    @Override
    public String getName() {
        return (String) attributes.get(nameAttributeKey);  // 사용자 이름 속성을 반환합니다.
    }

    // 이메일 반환 메서드 추가
    public String getEmail() {
        return (String) attributes.get("email"); // 사용자 이메일 속성을 반환합니다.
    }
    
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
