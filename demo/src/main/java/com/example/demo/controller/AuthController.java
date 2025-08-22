package com.example.demo.controller;

import com.example.demo.entity.Account;
import com.example.demo.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @GetMapping("/login")
    public String login() {
        // Trả về tên file template (admin.html)
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        // Trả về tên file template (admin.html)
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute Account account,
                           @RequestParam("repeatPassword") String repeatPassword) {
        try {
            if (account.getPassword().equals(repeatPassword)) {
                account.setAdmin(false);
                account.setPassword(passwordEncoder.encode(account.getPassword()));
                accountRepository.save(account);
                return "redirect:/login";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "register";
    }
    @GetMapping("/blogs")
    public String blog() {
        // Trả về tên file template (admin.html)
        return "blog";
    }
    @GetMapping("/abouts")
    public String about() {
        // Trả về tên file template (admin.html)
        return "about";
    }
    @GetMapping("/contacts")
    public String contact() {
        // Trả về tên file template (admin.html)
        return "contact";
    }

}
