package com.example.demo.entity;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "Inquiry_Categories")
public class InquiryCategoriesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_category_id")
    private int inquiryCategoryId;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @OneToMany(mappedBy = "inquiryCategory")
    private Set<InquiryEntity> inquiries;

    // Getters and Setters

    public int getInquiryCategoryId() {
        return inquiryCategoryId;
    }

    public void setInquiryCategoryId(int inquiryCategoryId) {
        this.inquiryCategoryId = inquiryCategoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<InquiryEntity> getInquiries() {
        return inquiries;
    }

    public void setInquiries(Set<InquiryEntity> inquiries) {
        this.inquiries = inquiries;
    }
}

