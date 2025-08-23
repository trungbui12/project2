package com.example.demo.controller;

import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    ProductRepository productRepository;
    @GetMapping("/")
    public String home(Model model, @RequestParam("page")Optional<Integer> page) {
        // Trả về tên file template (home.html)
        List<Category> categories = categoryRepository.findByActive(true, Sort.by(Sort.Direction.DESC, "id"));
        Pageable pageable = PageRequest.of(page.orElse(0), 16, Sort.by(Sort.Direction.DESC, "id"));
        Page<Product> products = productRepository.findByActive(true, pageable);

        model.addAttribute("categories", categories);

        model.addAttribute("products", products);
        model.addAttribute("page", page.orElse(0) +1);
        model.addAttribute("totalPage", products.getTotalPages());
        model.addAttribute("active", "home");
        return "home";
    }
//    tim kiem
@RequestMapping("/search")
public String search(Model model, @RequestParam("keyword") String keyword, @RequestParam("page")Optional<Integer> page) {
    // Trả về tên file template (home.html)
    List<Category> categories = categoryRepository.findByActive(true, Sort.by(Sort.Direction.DESC, "id"));
    Pageable pageable = PageRequest.of(page.orElse(0), 16, Sort.by(Sort.Direction.DESC, "id"));
    Page<Product> products = productRepository.findByKeyWord("%" +keyword + "%", pageable);

    model.addAttribute("categories", categories);

    model.addAttribute("products", products);
    model.addAttribute("page", page.orElse(0) +1);
    model.addAttribute("totalPage", products.getTotalPages());
    return "search";
}
}
