package com.example.demo.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
	@NoArgsConstructor
	@AllArgsConstructor
	@Entity
	@Builder
	@Getter
	@Setter
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
	    
	    @Column(name = "role",length = 20)
	    private String role; // ROLE_USER, ROLE_ADMIN 등
	    
	    
	    
	    @PrePersist
	    protected void onCreate() {
	        createdAt = LocalDateTime.now();
	        updatedAt = LocalDateTime.now();
	    }

	    @PreUpdate
	    protected void onUpdate() {
	        updatedAt = LocalDateTime.now();
	    }
	    
	    public UsersEntity (String username, String socialProvider, String email) {
	    	this.username = username;
	    	this.socialProvider = socialProvider;
	    	this.email = email;
	    	this.password ="일단 나중에 설정ㅇ";
	    	this.isSocialLogin = true;	    
	    }
	    
	    

	    
	    
	    public String getRole() {
	        return role;
	    }
	    
	   
}
