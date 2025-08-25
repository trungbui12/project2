package com.example.demo.controller;

import com.example.demo.entity.Account;
import com.example.demo.entity.Order;
import com.example.demo.entity.Product;
import com.example.demo.repository.OrderDetailRepository;
import com.example.demo.repository.OrderRepository;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class OrderController {
    @Autowired
    HttpSession session;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderDetailRepository orderDetailRepository;

    @RequestMapping("/orders")
    public String orders(Model model, @RequestParam("page")Optional<Integer> page){
        Account account = (Account) session.getAttribute("account");
        if (account == null) {
            return "redirect:/login";
        }
        Pageable pageable = PageRequest.of(page.orElse(0), 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<Order> orders = orderRepository.findByAccount(account, pageable);

        model.addAttribute("orders", orders.getContent());
        model.addAttribute("page", page.orElse(0) +1);
        model.addAttribute("totalPage", orders.getTotalPages());
        model.addAttribute("active", "home");
        return "order";
    }
    @RequestMapping("/orders/detail/{id}")
    public String orderDetail(Model model, @PathVariable("id")Integer id){
        Account account = (Account) session.getAttribute("account");
        if (account == null) {
            return "redirect:/login";
        }
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null || order.getAccount().getId() != account.getId()){
            return "redirect:/404";
        }
        model.addAttribute("order", order);
        return "order-detail";
    }
    @RequestMapping("/orders/cancel/{id}")
    public String orderCancel(Model model, @PathVariable("id")Integer id){
        Account account = (Account) session.getAttribute("account");
        if (account == null) {
            return "redirect:/login";
        }
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null || order.getAccount().getId() != account.getId()){
            return "redirect:/404";
        }
        order.setStatus(6);
        orderRepository.save(order);
        return "redirect:/orders";
    }
    @RequestMapping("/orders/receive/{id}")
    public String orderUpdate(Model model, @PathVariable("id")Integer id){
        Account account = (Account) session.getAttribute("account");
        if (account == null) {
            return "redirect:/login";
        }
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null || order.getAccount().getId() != account.getId()){
            return "redirect:/404";
        }
        order.setStatus(5);
        orderRepository.save(order);
        return "redirect:/orders";
    }
}
