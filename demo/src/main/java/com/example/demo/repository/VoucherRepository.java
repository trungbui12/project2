package com.example.demo.repository;

import com.example.demo.entity.Account;
import com.example.demo.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoucherRepository extends JpaRepository<Voucher, Integer> {
}
