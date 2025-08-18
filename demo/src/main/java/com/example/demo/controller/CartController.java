package com.example.demo.controller;

import com.example.demo.entity.Account;
import com.example.demo.entity.CartDetail;
import com.example.demo.entity.Product;
import com.example.demo.entity.Voucher;
import com.example.demo.repository.CartDetailRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.VoucherRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CartController {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CartDetailRepository cartDetailRepository;
    @Autowired
    VoucherRepository voucherRepository;
    @Autowired
    HttpSession session;

    @RequestMapping("/carts/add")
    public String add(RedirectAttributes redirectAttributes, @RequestParam("quantity") Integer quantity, @RequestParam("productSlug") String productSlug) {
        Account account = (Account) session.getAttribute("account");
        if (account == null) {
            return "redirect:/login";
        }
        Product product = productRepository.findByActiveAndSlug(true, productSlug);
        if (product == null) {
            return "redirect:/carts";
        }

        CartDetail cartDetail = cartDetailRepository.findByAccountAndProduct(account, product);
        if (cartDetail != null) {
            cartDetail.setQuantity(cartDetail.getQuantity() + quantity);
        } else {
            cartDetail = new CartDetail();
            cartDetail.setAccount(account);
            cartDetail.setProduct(product);
            cartDetail.setQuantity(quantity);
        }
        if (cartDetail.getQuantity() > product.getQuantity()) {
            redirectAttributes.addFlashAttribute("errorQuantity", "So luong khong du");
            return "redirect:/products/" + productSlug;
        }
        cartDetailRepository.save(cartDetail);
        return "redirect:/carts";
    }

    @RequestMapping("/carts")
    public String carts(Model model) {
        Account account = (Account) session.getAttribute("account");
        if (account == null) {
            return "redirect:/login";
        }
        List<CartDetail> cartDetails = cartDetailRepository.findByAccount(account);
        List<Voucher> vouchers = voucherRepository.findVoucherValidList(Sort.by(Sort.Direction.DESC, "discountPercent"));
        model.addAttribute("cartDetails", cartDetails);
        model.addAttribute("vouchers", vouchers);
        return "cart";
    }

    @RequestMapping("/carts/remove/{id}")
    public String remove(Model model, @PathVariable("id") Integer id) {
        Account account = (Account) session.getAttribute("account");
        if (account == null) {
            return "redirect:/login";
        }
        CartDetail cartDetail = cartDetailRepository.findById(id).orElse(null);
        if (account.getId() != cartDetail.getAccount().getId()) {
            return "redirect:/carts";
        }
        cartDetailRepository.delete(cartDetail);
        return "redirect:/carts";
    }
    @RequestMapping("/carts/update")
    public String remove(RedirectAttributes redirectAttributes, @RequestParam("id") Integer id, @RequestParam("quantity")Integer quantity) {
        Account account = (Account) session.getAttribute("account");
        if (account == null) {
            return "redirect:/login";
        }
        CartDetail cartDetail = cartDetailRepository.findById(id).orElse(null);
        if (account.getId() != cartDetail.getAccount().getId()) {
            return "redirect:/carts";
        }
        if (cartDetail != null){

            if (quantity > cartDetail.getProduct().getQuantity()){
                redirectAttributes.addFlashAttribute("errorQuantity", cartDetail.getId());
                cartDetail.setQuantity(cartDetail.getProduct().getQuantity());
            }else {
                cartDetail.setQuantity(quantity);
            }
            cartDetailRepository.save(cartDetail);
        }
        return "redirect:/carts";
    }
}