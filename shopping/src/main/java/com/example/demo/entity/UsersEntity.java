package com.example.demo.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

	@Entity
	@Table(name = "Users", uniqueConstraints = {
	    @UniqueConstraint(columnNames = "username"),
	    @UniqueConstraint(columnNames = "email")
	})
	public class UsersEntity {
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "user_id")
	    private int userId;

	    @Column(name = "username", nullable = false, length = 50)
	    private String username;

	    @Column(name = "password", nullable = false, length = 255)
	    private String password;

	    @Column(name = "email", nullable = false, length = 100)
	    private String email;

	    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	    private LocalDateTime createdAt;

	    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
	    private LocalDateTime updatedAt;

	    @Column(name = "is_social_login", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
	    private boolean isSocialLogin;

	    @Column(name = "social_provider", length = 50)
	    private String socialProvider;

	    // Getters and Setters

	    public int getUserId() {
	        return userId;
	    }

	    public void setUserId(int userId) {
	        this.userId = userId;
	    }

	    public String getUsername() {
	        return username;
	    }

	    public void setUsername(String username) {
	        this.username = username;
	    }

	    public String getPassword() {
	        return password;
	    }

	    public void setPassword(String password) {
	        this.password = password;
	    }

	    public String getEmail() {
	        return email;
	    }

	    public void setEmail(String email) {
	        this.email = email;
	    }

	    public LocalDateTime getCreatedAt() {
	        return createdAt;
	    }

	    public void setCreatedAt(LocalDateTime createdAt) {
	        this.createdAt = createdAt;
	    }

	    public LocalDateTime getUpdatedAt() {
	        return updatedAt;
	    }

	    public void setUpdatedAt(LocalDateTime updatedAt) {
	        this.updatedAt = updatedAt;
	    }

	    public boolean isSocialLogin() {
	        return isSocialLogin;
	    }

	    public void setSocialLogin(boolean socialLogin) {
	        isSocialLogin = socialLogin;
	    }

	    public String getSocialProvider() {
	        return socialProvider;
	    }

	    public void setSocialProvider(String socialProvider) {
	        this.socialProvider = socialProvider;
	    }
}
