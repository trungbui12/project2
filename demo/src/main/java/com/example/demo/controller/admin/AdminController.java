package com.example.demo.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AdminController {

    @RequestMapping("/admin")
    public String home(Model model) {
        // Trả về tên file template (admin.html)
        model.addAttribute("active", "index");
        return "admin/index";
    }
}
