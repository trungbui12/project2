package com.example.demo.controller.admin;

import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class CategoryController {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @RequestMapping("/admin/categories")
    public String index(Model model){
        List<Category> categories = categoryRepository.findByActive(true, Sort.by(Sort.Direction.DESC,"id"));
        model.addAttribute("active","category");
        model.addAttribute("categories", categories);
        return "admin/category/list";
    }

//    goi form them moi
    @RequestMapping("/admin/categories/add")
    public String add(Model model){
        model.addAttribute("active","category");
        model.addAttribute("category", new Category());
        return "admin/category/add";
    }
    @PostMapping("/admin/categories/add")
    public String insert(Model model,
                         @Valid @ModelAttribute Category category,
                         Errors errors) {

        if (errors.hasErrors()) {
            model.addAttribute("active", "category");
            model.addAttribute("message", "Vui lòng nhập đầy đủ thông tin");
            return "admin/category/add";
        }

        Category cate = categoryRepository.findByActiveAndSlug(true, category.getSlug());
        if (cate != null) {
            model.addAttribute("active", "category");
            model.addAttribute("message", "Ten loai da ton tai");
            model.addAttribute("message", "Slug đã tồn tại");
            return "admin/category/add";
        }
        category.setActive(true);
        category.setParentId(0);
        categoryRepository.save(category);
        return "redirect:/admin/categories";
    }
    @RequestMapping("/admin/categories/edit/{id}")
    public String edit(Model model, @PathVariable("id") Integer id) {
        Category category = categoryRepository.findById(id).orElse(null);

        model.addAttribute("category", category);
        model.addAttribute("active", "category");

        return "admin/category/edit";
    }
//    update
    @PostMapping("/admin/categories/update")
    public String update(Model model,
                         @Valid @ModelAttribute Category category,
                         Errors errors) {

        if (errors.hasErrors()) {
            model.addAttribute("active", "category");
            model.addAttribute("message", "Vui lòng nhập đầy đủ thông tin");
            return "admin/category/edit";
        }

        Category cate = categoryRepository.findByActiveAndSlug(true, category.getSlug());
        if (cate != null && cate.getId() !=category.getId()) {
            model.addAttribute("active", "category");
            model.addAttribute("message", "Ten loai da ton tai");
            model.addAttribute("message", "Slug đã tồn tại");
            return "admin/category/edit";
        }
        category.setActive(true);
        category.setParentId(0);
        categoryRepository.save(category);
        return "redirect:/admin/categories";
    }

    @RequestMapping("/admin/categories/delete/{id}")
    public String delete(Model model, @PathVariable("id") Integer id) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category.getProducts().size() > 0){
            for (Product product : category.getProducts()){
                product.setActive(false);
                productRepository.save(product);
            }
            category.setActive(false);
            categoryRepository.save(category);
        }else {
            categoryRepository.delete(category);
        }


        return "redirect:/admin/categories";
    }

}
