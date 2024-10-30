package com.example.demo.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDTO {

	private int productId;
	private String name;
	private String description;
	private int price;
	private int stock;
	private int categoryId;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
