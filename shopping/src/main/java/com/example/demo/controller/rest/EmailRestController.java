package com.example.demo.controller.rest;

import com.example.demo.dto.EmailCheckDTO;
import com.example.demo.dto.EmailRequestDTO;
import com.example.demo.service.MailSendService;
import org.hibernate.annotations.Check;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class EmailRestController {
	private final MailSendService mailService;
    @PostMapping ("/mailSend")
    public String mailSend(@RequestBody @Valid EmailRequestDTO emailDto){
        System.out.println("이메일 인증 이메일 :"+emailDto.getEmail());
        return mailService.joinEmail(emailDto.getEmail());
    }
}
