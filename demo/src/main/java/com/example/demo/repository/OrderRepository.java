package com.example.demo.repository;

import com.example.demo.entity.Account;
import com.example.demo.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    public List<Order> findByAccount(Account account);
    @Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END " +
            "FROM Order o JOIN o.orderDetails od " +
            "WHERE o.account.id = :accountId AND od.product.id = :productId")
    boolean hasPurchasedProduct(@Param("accountId") Integer accountId,
                                @Param("productId") Integer productId);
    public Page<Order> findByAccount(Account account, Pageable pageable);
}
