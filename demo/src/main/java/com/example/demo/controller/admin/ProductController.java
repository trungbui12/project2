package com.example.demo.controller.admin;

import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import jakarta.servlet.ServletContext;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Controller
public class ProductController {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    ServletContext servletContext;

    @RequestMapping("/admin/products")
    public String index(Model model) {
        List<Product> products = productRepository.findByActive(true);
        model.addAttribute("products", products);
        model.addAttribute("active", "product");
        return "admin/products/list";
    }
    //    from them moi
    @RequestMapping("/admin/products/add")
    public String showAddProductForm(Model model) {
        // Đánh dấu tab đang hoạt động là "product"
        model.addAttribute("active", "product");

        // Tạo một đối tượng Product mới để binding với form
        model.addAttribute("product", new Product());

        // Lấy danh sách các danh mục đang hoạt động, sắp xếp theo ID giảm dần
        List<Category> activeCategories = categoryRepository.findByActive(true, Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("categories", activeCategories);

        // Trả về tên view để hiển thị form thêm sản phẩm
        return "admin/products/add";
    }
    @PostMapping("/admin/products/add")
    public String insert(Model model, @Valid @ModelAttribute Product product, Errors errors, @RequestParam("imageProduct") MultipartFile image) {
        model.addAttribute("active", "product");
        if(errors.hasErrors()){
            model.addAttribute("message", "Vui long nhap day du thong tin");
        }
        try{
            if (!image.isEmpty()) {
                String fileName = image.getOriginalFilename();
                String filePath = servletContext.getRealPath("/images/" + fileName);
                if (!Files.exists(Path.of(filePath))) {
                    Files.createDirectories(Path.of(filePath));
                }
                File file = new File(filePath);
                image.transferTo(file);
                product.setImage(fileName);
            }
            Category category = categoryRepository.findById(product.getCategory().getId()).orElse(null);
            product.setCategory(category);
            product.setActive(true);
            productRepository.save(product);
            return "redirect:/admin/products";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "admin/products/add";
    }
    //    from edit
    @RequestMapping("/admin/products/edit/{id}")
    public String edit(Model model, @PathVariable("id") Integer id) {
        // Đánh dấu tab đang hoạt động là "product"
        model.addAttribute("active", "product");
        Product product = productRepository.findById(id).orElse(null);

        // Tạo một đối tượng Product mới để binding với form
        model.addAttribute("product",product);

        // Lấy danh sách các danh mục đang hoạt động, sắp xếp theo ID giảm dần
        List<Category> categories = categoryRepository.findByActive(true, Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("categories", categories);

        // Trả về tên view để hiển thị form thêm sản phẩm
        return "admin/products/edit";
    }
//    form cap nhat
    @PostMapping("/admin/products/update")
    public String update(Model model, @Valid @ModelAttribute Product product, Errors errors, @RequestParam("imageProduct") MultipartFile image) {
        model.addAttribute("active", "product");
        if(errors.hasErrors()){
            model.addAttribute("message", "Vui long nhap day du thong tin");
        }
        try{
            if (!image.isEmpty()) {
                String fileName = image.getOriginalFilename();
                String filePath = servletContext.getRealPath("/images/" + fileName);
                if (!Files.exists(Path.of(filePath))) {
                    Files.createDirectories(Path.of(filePath));
                }
                File file = new File(filePath);
                image.transferTo(file);
                product.setImage(fileName);
            }
            Category category = categoryRepository.findById(product.getCategory().getId()).orElse(null);
            product.setCategory(category);
            product.setActive(true);
            productRepository.save(product);
            return "redirect:/admin/products";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "admin/products/add";
    }
//    form delete
    @RequestMapping("/admin/products/delete/{id}")
    public String delete(Model model, @PathVariable("id") Integer id){
        model.addAttribute("active","product");
        Product product = productRepository.findById(id).orElse(null);
        product.setActive(false);
        productRepository.save(product);
        return "redirect:/admin/products";
    }

}