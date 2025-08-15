package com.example.demo.repository;

import com.example.demo.entity.Account;
import com.example.demo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    public List<Product> findByActive(Boolean active);
}
