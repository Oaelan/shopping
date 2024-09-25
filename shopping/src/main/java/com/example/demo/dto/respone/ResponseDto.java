package com.example.demo.dto.respone;

import org.springframework.http.ResponseEntity;

import com.example.demo.enums.ResponseCode;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseDto {
	
	private String code;
	private String message;
	
	
	public static ResponseEntity<ResponseDto> validationFail() {
        ResponseDto response = new ResponseDto();
        response.setCode(ResponseCode.VALIDATION_FAIL.name());
        response.setMessage("Validation failed.");
        return ResponseEntity.badRequest().body(response);
    }

}
