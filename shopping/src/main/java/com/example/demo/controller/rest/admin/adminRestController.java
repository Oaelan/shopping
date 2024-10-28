package com.example.demo.controller.rest.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class adminRestController {

	@GetMapping("/api/admin/product/category")
	public String getMethodName() {
		return new String();
	}
	
}
