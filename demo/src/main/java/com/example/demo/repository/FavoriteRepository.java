package com.example.demo.repository;

import com.example.demo.entity.Account;
import com.example.demo.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
    public Page<Favorite> findByAccount(Account account, Pageable pageable);
}
