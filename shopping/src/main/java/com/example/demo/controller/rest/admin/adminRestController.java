package com.example.demo.controller.rest.admin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.CategoryDTO;
import com.example.demo.entity.CategoriesEntity;
import com.example.demo.service.admin.ProductService;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController
public class adminRestController {
	
	@Autowired
	ProductService productService;
	
	//등록된 카테고리 리스트 가져오기
	@GetMapping("/api/admin/test/category")
	public ResponseEntity<List<CategoryDTO>> getCategoryLists() {
	    List<CategoryDTO> categoryLists = productService.getAllCategories(); // 카테고리 리스트 가져오기
	    return ResponseEntity.ok(categoryLists); // 200 OK 응답으로 카테고리 리스트 반환
	}
	
	
	private String uploadDir = "/Users/aelanoh/Desktop/ShoppingProject_P_Imgs";
	
	@PostMapping("/api/admin/test/upload")
	// 상품 정보 테이블에 추가 되는 로직 추가해야함!
	public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
	    try {
	    	// 업로드된 파일 이름 가져오기
	        String originalFilename = file.getOriginalFilename();
	    	log.info("file: " + originalFilename); // 업로드된 파일 이름 로그
	    	// 파일 확장자 추출
	        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
	        log.info("파일 확장: " + fileExtension); // 업로드된 파일 이름 로그	
	    	
	    	
	        // 고유한 파일명 생성
	        String filename = UUID.randomUUID().toString() + fileExtension; // 수정된 부분
	        Path filePath = Paths.get(uploadDir, filename); // 수정된 부분
	        
	        if (!Files.exists(filePath)) {
	           log.info("해당 파일 경로가 없습니다");
	        }
	        
	        // 파일 저장
	        Files.write(filePath, file.getBytes());

	        return ResponseEntity.ok("File uploaded successfully: " + filename);
	    } catch (IOException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed");
	    }
	}
}
