package com.example.demo.service.admin.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.CategoryDTO;
import com.example.demo.entity.CategoriesEntity;
import com.example.demo.repository.CategoriesRepository;
import com.example.demo.service.admin.ProductService;

import io.swagger.v3.oas.annotations.servers.Server;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j

public class ProductSerivceImpl implements ProductService {

	@Autowired
	private CategoriesRepository CategoriesRepo;

	public List<CategoryDTO> getAllCategories() {
		List<CategoriesEntity> allCategories = CategoriesRepo.findAll();
		List<CategoryDTO> categoryLists = new ArrayList<>(); // List 초기화

		// 모든 카테고리를 반복하여 DTO로 변환
		for (CategoriesEntity category : allCategories) {
			CategoryDTO cateDTO = new CategoryDTO();
			cateDTO.setCategoryId(category.getCategoryId());
			cateDTO.setName(category.getName());
			
			// 부모 카테고리 확인 후 ID 설정
			if (category.getParentCategory() != null) {
				cateDTO.setParentCategoryId(category.getParentCategory().getCategoryId());
			} else {
				cateDTO.setParentCategoryId(null); // 부모가 없는 경우
			}
			categoryLists.add(cateDTO);
		}

		return categoryLists;
	};
}
