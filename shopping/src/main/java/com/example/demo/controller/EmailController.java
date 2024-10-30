package com.example.demo.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.EmailCheckDTO;
import com.example.demo.dto.EmailRequestDTO;
import com.example.demo.service.MailSendService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class EmailController {

	private final MailSendService mailService;
    
    @PostMapping("/mailauthCheck")
    public String AuthCheck(@RequestBody @Valid EmailCheckDTO emailCheckDto){
        Boolean Checked=mailService.CheckAuthNum(emailCheckDto.getEmail(),emailCheckDto.getAuthNum());
        if(Checked){
            return "ok";
        }
        else{
            throw new NullPointerException("뭔가 잘못!");
        }
    }
}
