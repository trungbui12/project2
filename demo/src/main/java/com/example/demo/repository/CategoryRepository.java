package com.example.demo.repository;

import com.example.demo.entity.Account;
import com.example.demo.entity.Category;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    public List<Category> findByActive(Boolean active, Sort sort);
    public Category findByActiveAndSlug(Boolean active, String slug);
}
