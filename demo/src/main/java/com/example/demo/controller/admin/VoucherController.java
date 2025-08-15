package com.example.demo.controller.admin;

import com.example.demo.entity.Voucher;
import com.example.demo.repository.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class VoucherController {
    @Autowired
    VoucherRepository voucherRepository;
    @RequestMapping("/admin/vouchers")
    public String index(Model model){
        List<Voucher> vouchers = voucherRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("vouchers", vouchers);
        model.addAttribute("active","voucher");
        return "admin/vouchers/list";
    }

    @RequestMapping("/admin/vouchers/add")
    public String add(Model model){
        model.addAttribute("voucher", new Voucher());
        model.addAttribute("active","voucher");
        return "admin/vouchers/add";
    }
}
