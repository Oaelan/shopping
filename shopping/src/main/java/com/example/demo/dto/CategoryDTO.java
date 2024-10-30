package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

public class CategoryDTO {
    private int categoryId;
    private String name;
    private Integer parentCategoryId; // 부모 카테고리 ID (nullable 가능)
}
