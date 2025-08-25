package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.service.GHNService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Date;
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
    @Autowired
    GHNService ghnService;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderDetailRepository orderDetailRepository;



    @RequestMapping("/carts/add")
    public String add(RedirectAttributes redirectAttributes, @RequestParam("quantity") Integer quantity, @RequestParam("productSlug") String productSlug) {
        Account account = (Account) session.getAttribute("account");
        if (account == null) {
            return "redirect:/login";
        }
        Product product = productRepository.findByActiveAndSlug(true, productSlug);
        if (product == null) {
            return "redirect:/carts/cartdemo";
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
        return "redirect:/carts/cartdemo";
    }

    @RequestMapping("/carts/cartdemo")
    public String cartdemo(Model model) {
        Account account = (Account) session.getAttribute("account");
        if (account == null) {
            return "redirect:/login";
        }
        List<CartDetail> cartDetails = cartDetailRepository.findByAccount(account);
        List<Integer>ids = new ArrayList<Integer>();
        List<Order> orders = orderRepository.findByAccount(account);
        for (Order order: orders){
            if (order.getVoucher()!=null ){
                ids.add(order.getVoucher().getId());
            }

        }
        List<Voucher> vouchers = voucherRepository.findVoucherValidList(ids,Sort.by(Sort.Direction.DESC, "discountPercent"));
        model.addAttribute("cartDetails", cartDetails);
        model.addAttribute("vouchers", vouchers);
        return "cartdemo";
    }

    @RequestMapping("/carts/cartdemo/remove/{id}")
    public String removedemo(Model model, @PathVariable("id") Integer id) {
        Account account = (Account) session.getAttribute("account");
        if (account == null) {
            return "redirect:/login";
        }
        CartDetail cartDetail = cartDetailRepository.findById(id).orElse(null);
        if (account.getId() != cartDetail.getAccount().getId()) {
            return "redirect:/carts/cartdemo";
        }
        cartDetailRepository.delete(cartDetail);
        return "redirect:/carts/cartdemo";
    }

    @RequestMapping("/carts/cartdemo/update")
    public String updatedemo(RedirectAttributes redirectAttributes, @RequestParam("id") Integer id, @RequestParam("quantity")Integer quantity) {
        Account account = (Account) session.getAttribute("account");
        if (account == null) {
            return "redirect:/login";
        }
        CartDetail cartDetail = cartDetailRepository.findById(id).orElse(null);
        if (account.getId() != cartDetail.getAccount().getId()) {
            return "redirect:/carts/cartdemo";
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
        return "redirect:/carts/cartdemo";
    }

    @RequestMapping("/carts")
    public String carts(Model model) {
        Account account = (Account) session.getAttribute("account");
        if (account == null) {
            return "redirect:/login";
        }
        List<CartDetail> cartDetails = cartDetailRepository.findByAccount(account);
        List<Integer>ids = new ArrayList<Integer>();
        List<Order> orders = orderRepository.findByAccount(account);
        for (Order order: orders){
            if (order.getVoucher()!=null ){
                ids.add(order.getVoucher().getId());
            }

        }
        List<Voucher> vouchers = voucherRepository.findVoucherValidList(ids,Sort.by(Sort.Direction.DESC, "discountPercent"));
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
    @RequestMapping("/carts/checkout")
    public String checkout(@RequestParam("ids") String[]ids,
                           @RequestParam("customerName") String fullName,
                           @RequestParam("customerPhone") String phoneNumber,
                           @RequestParam("customerEmail") String email,
                           @RequestParam("provinceSelect") int provinceSelect,
                           @RequestParam("districtSelect") int districtSelect,
                           @RequestParam("wardSelect") String wardSelect,
                           @RequestParam("fullAddress") String fullAddress,
                           @RequestParam("voucher") Integer voucherId,
                           @RequestParam("paymentMethod") String paymentMethod){
        Account account = (Account) session.getAttribute("account");
        if (account == null) {
            return "redirect:/login";
        }
        int total = 0;
        for (String id : ids){
            CartDetail cartDetail = cartDetailRepository.findById(Integer.parseInt(id)).orElse(null);
            total += cartDetail.getQuantity() * cartDetail.getProduct().getPrice();
        }
        Voucher voucher = voucherRepository.findById(voucherId).orElse(null);
        int discount = total * voucher.getDiscountPercent()/100;


        int feeShip = ghnService.getShippingFee(districtSelect, wardSelect);
        Order order = new Order();
        order.setCreatedAt(new Date());
        order.setAccount(account);
        order.setVoucher(voucher);
        order.setDiscount(discount);
        order.setFeeShip(feeShip);
        order.setShipAddress(fullAddress);
        order.setTotal(total);
        order.setFullName(fullName);
        order.setPhoneNumber(phoneNumber);
        order.setEmail(email);
        if(paymentMethod.equals("COD")){
            order.setPaymentMethod(0);
            order.setPaymentStatus(0);
            order.setStatus(1);
            orderRepository.save(order);
            for (String id : ids){
                CartDetail cartDetail = cartDetailRepository.findById(Integer.parseInt(id)).orElse(null);
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrder(order);
                orderDetail.setProduct(cartDetail.getProduct());
                orderDetail.setPrice(cartDetail.getProduct().getPrice());
                orderDetail.setQuantity(cartDetail.getQuantity());
                orderDetailRepository.save(orderDetail);
                cartDetailRepository.delete(cartDetail);
            }
            return "redirect:/orders/detail/" + order.getId();
        }
        if (paymentMethod.equals("VIETQR")) {
            order.setPaymentMethod(1); // VietQR
            order.setPaymentStatus(0); // chưa xác nhận thanh toán
            order.setStatus(1);        // chờ xử lý
        }

        orderRepository.save(order);

        for (String id : ids) {
            CartDetail cartDetail = cartDetailRepository.findById(Integer.parseInt(id)).orElse(null);
            if (cartDetail != null) {
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrder(order);
                orderDetail.setProduct(cartDetail.getProduct());
                orderDetail.setPrice(cartDetail.getProduct().getPrice());
                orderDetail.setQuantity(cartDetail.getQuantity());
                orderDetailRepository.save(orderDetail);
                cartDetailRepository.delete(cartDetail);
            }
            return "redirect:/orders/detail/" + order.getId();
        }
        return "redirect:/carts";
    }
}