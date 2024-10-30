package com.example.demo.config;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import java.util.Properties;

@Configuration
public class EmailConfig {
	@Bean
    public JavaMailSender mailSender() {//JAVA MAILSENDER 인터페이스를 구현한 객체를 빈으로 등록하기 위함.

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();//JavaMailSender 의 구현체를 생성하고
        mailSender.setHost("smtp.gmail.com");// 속성을 넣기 시작합니다. 이메일 전송에 사용할 SMTP 서버 호스트를 설정
        mailSender.setPort(587);// 587로 포트를 지정
        mailSender.setUsername("zzxx111588@gmail.com");//구글계정을 넣습니다.
        mailSender.setPassword("anpf zwwc bhpe goqk");//구글 앱 비밀번호를 넣습니다.

        Properties javaMailProperties = new Properties();//JavaMail의 속성을 설정하기 위해 Properties 객체를 생성
        javaMailProperties.put("mail.transport.protocol", "smtp");
        javaMailProperties.put("mail.smtp.auth", "true");
        javaMailProperties.put("mail.smtp.starttls.enable", "true"); // STARTTLS 사용
        javaMailProperties.put("mail.debug", "true");
        javaMailProperties.put("mail.smtp.ssl.trust", "smtp.gmail.com"); // SMTP 서버에 대한 신뢰 설정

        mailSender.setJavaMailProperties(javaMailProperties);

        return mailSender;
    }
}
