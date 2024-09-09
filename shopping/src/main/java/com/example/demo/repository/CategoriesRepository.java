package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.CategoriesEntity;

public interface CategoriesRepository extends JpaRepository<CategoriesEntity, Integer> {

}
