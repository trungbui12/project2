package com.example.demo.controller.admin;

import com.example.demo.entity.Voucher;
import com.example.demo.repository.VoucherRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
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
//form trang add
    @RequestMapping("/admin/vouchers/add")
    public String add(Model model){
        model.addAttribute("voucher", new Voucher());
        model.addAttribute("active","voucher");
        return "admin/vouchers/add";
    }
//    form insert
    @PostMapping("/admin/vouchers/add")
    public String insert(Model model, @Valid @ModelAttribute Voucher voucher, Errors errors){
        if (errors.hasErrors()){
            model.addAttribute("message","Vui lòng kiểm tra thông tin nhập");
        }
        Voucher voucher1 = voucherRepository.findByCode(voucher.getCode());
        try {
            if (voucher1 == null) {
                voucher.setCreatedAt(new Date());
                voucher.setActived(true);
                voucherRepository.save(voucher);
                return "redirect:/admin/vouchers";
            }else {
                model.addAttribute("codeError", "Mã giảm giá đã tồn tại");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        model.addAttribute("active","voucher");
        return "admin/vouchers/add";

    }
//    form edit
    @RequestMapping("/admin/vouchers/edit/{id}")
    public String edit(Model model, @PathVariable("id") Integer id){
        Voucher voucher = voucherRepository.findById(id).orElse(null);
        model.addAttribute("voucher", voucher);
        model.addAttribute("active","voucher");
        return "admin/vouchers/edit";
    }
    //    form update
    @PostMapping("/admin/vouchers/update")
    public String update(Model model, @Valid @ModelAttribute Voucher voucher, Errors errors){
        if (errors.hasErrors()){
            model.addAttribute("message","Vui lòng kiểm tra thông tin nhập");
        }
        try {
                voucherRepository.save(voucher);
                return "redirect:/admin/vouchers";
        }catch (Exception e){
            e.printStackTrace();
        }
        model.addAttribute("active","voucher");
        return "admin/vouchers/edit";
    }
//    form active or unactive
    @RequestMapping("/admin/vouchers/active/{id}")
    public String active(Model model, @PathVariable("id") Integer id){
        Voucher voucher = voucherRepository.findById(id).orElse(null);
        voucher.setActived(!voucher.getActived());
        voucherRepository.save(voucher);
        model.addAttribute("active","voucher");
        return "redirect:/admin/vouchers";
    }
//    form delete
    @RequestMapping("/admin/vouchers/delete/{id}")
    public String delete(Model model, @PathVariable("id") Integer id){
        Voucher voucher = voucherRepository.findById(id).orElse(null);
        if (voucher.getOrders().size() > 0){
            voucher.setActived(false);
            voucherRepository.save(voucher);
        }else{
            voucherRepository.delete(voucher);
        }
        return "redirect:/admin/vouchers";
    }
}
