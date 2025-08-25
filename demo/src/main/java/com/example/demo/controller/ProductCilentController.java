package com.example.demo.controller;

import com.example.demo.entity.Account;
import com.example.demo.entity.Product;
import com.example.demo.entity.Review;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class ProductCilentController {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    AccountRepository accountRepository;

    // Danh sách sản phẩm
    @RequestMapping("/products")
    public String index(Model model, @RequestParam("page") Optional<Integer> page) {
        List categories = categoryRepository.findByActive(true, Sort.by(Sort.Direction.DESC, "id"));
        Pageable pageable = PageRequest.of(page.orElse(0), 200, Sort.by(Sort.Direction.DESC, "id"));
        Page<Product> products = productRepository.findByActive(true, pageable);

        model.addAttribute("categories", categories);
        model.addAttribute("products", products);
        model.addAttribute("page", page.orElse(0) + 1);
        model.addAttribute("totalPage", products.getTotalPages());
        model.addAttribute("active", "product");

        return "product";
    }

    // Chi tiết sản phẩm
    @RequestMapping("/products/{slug}")
    public String productDetail(Model model,
                                @PathVariable("slug") String slug,
                                @SessionAttribute(name = "account", required = false) Account account) {

        Product product = productRepository.findByActiveAndSlug(true, slug);
        model.addAttribute("active", "product");
        model.addAttribute("product", product);

        if (product != null) {
            List<Product> relateProducts = productRepository.findByActiveAndCategory(true,
                    product.getCategory(), Sort.by(Sort.Direction.DESC, "id"));
            model.addAttribute("relateProducts", relateProducts);
            model.addAttribute("reviews", product.getReviews());

            // Kiểm tra xem account đã mua sản phẩm chưa
            boolean canReview = false;
            if (account != null) {
                canReview = orderRepository.hasPurchasedProduct(account.getId(), product.getId());
            }
            model.addAttribute("canReview", canReview);
        }

        return "product-detail";
    }

    // Thêm review
    @PostMapping("/products/{slug}/review")
    public String addReview(@PathVariable("slug") String slug,
                            @RequestParam String name,
                            @RequestParam String email,
                            @RequestParam int rating,
                            @RequestParam String content,
                            @SessionAttribute(name = "account", required = false) Account account) {

        Product product = productRepository.findByActiveAndSlug(true, slug);

        if (product != null && account != null
                && orderRepository.hasPurchasedProduct(account.getId(), product.getId())) {

            Review review = new Review();
            review.setName(name);
            review.setEmail(email);
            review.setRating(rating);
            review.setContent(content);
            review.setProduct(product);

            product.getReviews().add(review);
            productRepository.save(product);
        }

        return "redirect:/products/" + slug;
    }
}
