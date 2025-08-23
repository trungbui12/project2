package com.example.demo.repository;

import com.example.demo.entity.Account;
import com.example.demo.entity.CartDetail;
import com.example.demo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartDetailRepository extends JpaRepository<CartDetail, Integer> {
    public CartDetail findByAccountAndProduct(Account account, Product product);
    List<CartDetail> findByAccount(Account account);
}
