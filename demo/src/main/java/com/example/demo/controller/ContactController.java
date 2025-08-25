package com.example.demo.controller;

import com.example.demo.entity.ContactMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ContactController {

    // Xử lý yêu cầu GET để hiển thị trang liên hệ
    @GetMapping("/contact")
    public String showContactPage(Model model) {
        model.addAttribute("contactMessage", new ContactMessage());
        return "contacts"; // Trả về tên của template HTML (contact.html)
    }

    // Xử lý yêu cầu POST khi người dùng gửi form
    @PostMapping("/contact")
    public String submitContactForm(@ModelAttribute("contactMessage") ContactMessage contactMessage, Model model) {
        try {
            // Logic để lưu dữ liệu vào cơ sở dữ liệu
            System.out.println("Email: " + contactMessage.getEmail());
            System.out.println("Tin nhắn: " + contactMessage.getMessage());

            // Lưu dữ liệu vào cơ sở dữ liệu ở đây (ví dụ: sử dụng JPA, Hibernate...)
            // contactService.save(contactMessage);

            // Thêm thông báo thành công vào model
            model.addAttribute("successMessage", "Tin nhắn của bạn đã được gửi thành công!");

            // Tạo một đối tượng mới để xóa dữ liệu trên form
            model.addAttribute("contactMessage", new ContactMessage());

        } catch (Exception e) {
            // Thêm thông báo lỗi vào model
            model.addAttribute("errorMessage", "Có lỗi xảy ra, vui lòng thử lại sau.");
        }

        // Trả về trang liên hệ
        return "contacts";
    }
}
