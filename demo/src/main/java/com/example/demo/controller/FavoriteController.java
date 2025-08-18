package com.example.demo.controller;

import com.example.demo.entity.Account;
import com.example.demo.entity.Category;
import com.example.demo.entity.Favorite;
import com.example.demo.entity.Product;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.FavoriteRepository;
import com.example.demo.repository.ProductRepository;
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


import java.util.List;
import java.util.Optional;

@Controller
public class FavoriteController {
    @Autowired
    FavoriteRepository favoriteRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    HttpSession session;
    @RequestMapping("/favorites")
    public String index(Model model, @RequestParam("page") Optional<Integer> page) {
        List<Category> categories = categoryRepository.findByActive(true, Sort.by(Sort.Direction.DESC, "id"));
        // Trả về tên file template (home.html)
        Account account = (Account) session.getAttribute("account");

        Pageable pageable = PageRequest.of(page.orElse(0), 16, Sort.by(Sort.Direction.DESC, "id"));
        Page<Favorite> favorites = favoriteRepository.findByAccount(account,pageable);

        model.addAttribute("categories", categories);
        model.addAttribute("favorites", favorites.getContent());
        model.addAttribute("page", page.orElse(0) +1);
        model.addAttribute("totalPage", favorites.getTotalPages());
        model.addAttribute("active", "favorite");
        return "favorite";
    }
    @RequestMapping("/favorites/add/{slug}")
    public String add(Model model, @PathVariable("slug") String slug) {
        Account account = (Account) session.getAttribute("account");
        Product product = productRepository.findByActiveAndSlug(true, slug);
        Favorite favorite = new Favorite();
        favorite.setAccount(account);
        favorite.setProduct(product);
        favoriteRepository.save(favorite);
        return "redirect:/favorites";
    }
}
