package com.example.demo.controller.admin;

import com.example.demo.entity.Order;
import com.example.demo.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class OrderManagementController {
    @Autowired
    OrderRepository orderRepository;
    @RequestMapping("/admin/orders")
    public String index(Model model){
        List<Order> orders = orderRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("orders", orders);
        model.addAttribute("active", "order");
        return "admin/orders/list";
    }
    @RequestMapping("/admin/orders/updateStatus/{id}/{status}")
    public String updateStatus(RedirectAttributes redirectAttributes, @PathVariable("id")Integer id, @PathVariable("status") Optional<Integer> status){
        Order order = orderRepository.findById(id).orElse(null);
        if (order != null && !status.isEmpty()){
            order.setStatus(status.get());
            orderRepository.save(order);
            redirectAttributes.addFlashAttribute("message","Cap nhat trang thai thanh cong");
        }
        return "redirect:/admin/orders";
    }
    @RequestMapping("/admin/orders/detail/{id}")
    public String orderDetail(Model model, @PathVariable("id")Integer id){
        Order order = orderRepository.findById(id).orElse(null);
        model.addAttribute("order", order);
        model.addAttribute("active", "order");
        return "admin/orders/detail";
    }
}
