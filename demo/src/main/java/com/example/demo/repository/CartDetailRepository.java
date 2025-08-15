package com.example.demo.repository;

import com.example.demo.entity.Account;
import com.example.demo.entity.CartDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartDetailRepository extends JpaRepository<CartDetail, Integer> {
}
