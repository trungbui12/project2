package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class BlogController {

    // Trang danh sách blog (công khai)
    @GetMapping("/blogs")
    public String blogList(Model model) {
        // Nếu có dữ liệu blog từ DB thì load ra, tạm thời demo với dữ liệu giả
        model.addAttribute("title", "Danh sách bài viết Blog");
        // Bạn có thể thêm list bài viết vào model
        // model.addAttribute("blogs", blogService.findAll());
        return "blog"; // trỏ đến file blog.html
    }

    // Trang chi tiết blog (công khai)
    @GetMapping("/blogs/detail")
    public String blogDetail(Model model) {
        model.addAttribute("title", "Chi tiết bài viết");
        return "blog-detail"; // ví dụ bạn có blog-detail.html
    }
}
