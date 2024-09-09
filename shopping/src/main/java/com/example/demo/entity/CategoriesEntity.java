package com.example.demo.entity;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "Categories")
public class CategoriesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private int categoryId;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "category_id", foreignKey = @ForeignKey(name = "Categories_ibfk_1"))
    private CategoriesEntity parentCategory;

    @OneToMany(mappedBy = "parentCategory")
    private Set<CategoriesEntity> subCategories;

    // Getters and Setters

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CategoriesEntity getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(CategoriesEntity parentCategory) {
        this.parentCategory = parentCategory;
    }

    public Set<CategoriesEntity> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(Set<CategoriesEntity> subCategories) {
        this.subCategories = subCategories;
    }
}

