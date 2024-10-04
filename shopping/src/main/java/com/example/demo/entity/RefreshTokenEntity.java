package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor // 기본 생성자 생성
@AllArgsConstructor // 모든 필드를 매개변수로 받는 생성자 생성
@Entity // JPA 엔티티 클래스임을 나타냄
@Builder // 빌더 패턴으로 객체를 생성할 수 있도록 지원
@Getter // 모든 필드에 대한 Getter 생성
@Setter // 모든 필드에 대한 Setter 생성
@Table(name = "refresh_tokens") // 엔티티가 "refresh_tokens"라는 이름의 테이블과 매핑됨
public class RefreshTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본키 자동 증가 전략 사용 (IDENTITY)
    @Column(name = "id") // 컬럼 이름은 "id"
    private int id; // 기본 키 (Refresh Token의 고유 식별자)

    @Column(name = "token", nullable = false, unique = true) // 토큰 값, 고유해야 하고 null을 허용하지 않음
    private String token;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP") // 토큰 생성 시각, 기본값으로 현재 시간 설정
    private LocalDateTime createdAt;

    @Column(name = "expires_at", columnDefinition = "TIMESTAMP") // 토큰 만료 시각
    private LocalDateTime expiresAt;

    @ManyToOne(fetch = FetchType.LAZY) // 여러 Refresh Token이 하나의 사용자와 연관됨 (다대일 관계)
    @JoinColumn(name = "email", referencedColumnName = "email", nullable = false) // UsersEntity의 email과 매핑
    private UsersEntity usersEntity; // Refresh Token이 속한 사용자 엔티티

    @PrePersist // 엔티티가 저장되기 전에 호출되는 메서드, 생성 시간을 설정
    protected void onCreate() {
        createdAt = LocalDateTime.now(); // 토큰 생성 시각을 현재 시간으로 설정
    }
}
