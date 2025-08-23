package com.example.demo.repository;

import com.example.demo.entity.Account;
import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    public List<Product> findByActive(Boolean active);
    public Page<Product> findByActive(boolean active, Pageable pageable);
    @Query("select p from Product p where p.active = true and p.name like :keyword")
    public Page<Product> findByKeyWord(@Param("keyword") String keyword, Pageable pageable);
    public Product findByActiveAndSlug(Boolean active, String slug);
    public List<Product> findByActiveAndCategory(Boolean active, Category category, Sort sort);
}
