package com.example.demo.repository;

import com.example.demo.entity.Account;
import com.example.demo.entity.Voucher;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VoucherRepository extends JpaRepository<Voucher, Integer> {
    Voucher findByCode(String code);
    @Query("select v from Voucher v where v.actived = true and startedAt <= CURRENT_TIMESTAMP and v.endAt >= CURRENT_TIMESTAMP and v.quantity > 0")
    List<Voucher> findVoucherValidList(Sort sort);
}
