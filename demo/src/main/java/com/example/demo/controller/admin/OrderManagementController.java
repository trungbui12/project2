package com.example.demo.controller.admin;

import com.example.demo.entity.Order;
import com.example.demo.repository.OrderRepository;
import com.example.demo.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class OrderManagementController {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    EmailService emailService;
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
        if (order != null && !status.isEmpty()) {
            order.setStatus(status.get());
            orderRepository.save(order);

            redirectAttributes.addFlashAttribute("message","Update order status successfully");

            try {
                String email = order.getEmail(); // Email khách
                String subject = "Update on your order #" + order.getId();
                String content = "<h3>Update on your order</h3>"
                        + "<p>Order ID: #" + order.getId() + "</p>";

                if (status.get() == 6) { // Cancelled
                    content += "<p>Your order has been <strong>cancelled</strong>.</p>"
                            + "<p>Reason for cancellation: <strong>" + order.getCancelReason() + "</strong></p>"
                            + "<p>If you have any questions, please contact our support.</p>";
                } else {
                    // Các trạng thái khác
                    String statusText = "";
                    switch (status.get()) {
                        case 2: statusText = "Processing"; break;
                        case 3: statusText = "Shipping"; break;
                        case 4: statusText = "Delivered"; break;
                        case 5: statusText = "Completed"; break;
                        default: statusText = "Pending Confirmation";
                    }
                    content += "<p>New Status: <strong>" + statusText + "</strong></p>";
                }

                content += "<p>Thank you for shopping with us!</p>";

                emailService.sendOrderConfirmation(email, subject, content);
            } catch (Exception e){
                e.printStackTrace();
            }
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
    @PostMapping("/admin/orders/cancel/{id}")
    public String cancelOrder(@PathVariable("id") Integer id,
                              @RequestParam("cancelReason") String cancelReason,
                              RedirectAttributes redirectAttributes) {
        Order order = orderRepository.findById(id).orElse(null);
        if(order != null){
            order.setStatus(6); // Cancelled
            order.setCancelReason(cancelReason); // Lưu lý do
            orderRepository.save(order);

            redirectAttributes.addFlashAttribute("message", "Order cancelled successfully");

            // Gửi mail cho khách
            try {
                String email = order.getEmail();
                String subject = "Your order #" + order.getId() + " has been cancelled";
                String content = "<h3>Your order has been cancelled</h3>"
                        + "<p>Order ID: #" + order.getId() + "</p>"
                        + "<p>Reason: <strong>" + cancelReason + "</strong></p>"
                        + "<p>If you have any questions, please contact our support.</p>";
                emailService.sendOrderConfirmation(email, subject, content);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "redirect:/admin/orders";
    }

}