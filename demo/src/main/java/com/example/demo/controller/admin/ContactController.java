package com.example.demo.controller.admin;

import com.example.demo.entity.Contact;
import com.example.demo.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ContactController {

    @Autowired
    private ContactRepository contactRepository;

    @GetMapping("/admin/contacts")
    public String listContacts(Model model){
        List<Contact> contacts = contactRepository.findAll();
        model.addAttribute("contacts", contacts);
        model.addAttribute("active", "contact");
        return "admin/contacts/list"; // file Thymeleaf hiển thị danh sách
    }
}
