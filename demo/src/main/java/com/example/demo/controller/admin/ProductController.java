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
@RequestMapping("/admin/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ServletContext servletContext;

    // Trang danh sách sản phẩm
    @GetMapping
    public String index(Model model) {
        List<Product> products = productRepository.findByActive(true);
        model.addAttribute("products", products);
        model.addAttribute("active", "product");
        return "admin/products/list";
    }

    // Form thêm mới
    @GetMapping("/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("active", "product");
        model.addAttribute("product", new Product());
        List<Category> categories = categoryRepository.findByActive(true, Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("categories", categories);
        return "admin/products/add";
    }

    // Xử lý thêm mới
    @PostMapping("/add")
    public String insert(Model model,
                         @Valid @ModelAttribute Product product,
                         Errors errors,
                         @RequestParam("imageProduct") MultipartFile image,
                         @RequestParam("imageProduct2") MultipartFile image2,
                         @RequestParam("imageProduct3") MultipartFile image3,
                         @RequestParam("imageSizeGuide") MultipartFile imageSizeGuide) {

        model.addAttribute("active", "product");

        if (errors.hasErrors()) {
            model.addAttribute("message", "Please fill in all required fields");
            return "admin/products/add";
        }

        try {
            saveImages(product, image, image2, image3, imageSizeGuide);
            Category category = categoryRepository.findById(product.getCategory().getId()).orElse(null);
            product.setCategory(category);
            product.setActive(true);
            productRepository.save(product);

            return "redirect:/admin/products";
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("message", "Error uploading images");
            return "admin/products/add";
        }
    }

    // Form edit
    @GetMapping("/edit/{id}")
    public String edit(Model model, @PathVariable("id") Integer id) {
        model.addAttribute("active", "product");
        Product product = productRepository.findById(id).orElse(null);
        model.addAttribute("product", product);

        List<Category> categories = categoryRepository.findByActive(true, Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("categories", categories);

        return "admin/products/edit";
    }

    // Xử lý cập nhật
    @PostMapping("/update")
    public String update(Model model,
                         @Valid @ModelAttribute Product product,
                         Errors errors,
                         @RequestParam("imageProduct") MultipartFile image,
                         @RequestParam("imageProduct2") MultipartFile image2,
                         @RequestParam("imageProduct3") MultipartFile image3,
                         @RequestParam("imageSizeGuide") MultipartFile imageSizeGuide) {

        model.addAttribute("active", "product");

        if (errors.hasErrors()) {
            model.addAttribute("message", "Please fill in all required fields");
            return "admin/products/edit";
        }

        try {
            saveImages(product, image, image2, image3, imageSizeGuide);
            Category category = categoryRepository.findById(product.getCategory().getId()).orElse(null);
            product.setCategory(category);
            product.setActive(true);
            productRepository.save(product);

            return "redirect:/admin/products";
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("message", "Error uploading images");
            return "admin/products/edit";
        }
    }

    // Xóa mềm
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product != null) {
            product.setActive(false);
            productRepository.save(product);
        }
        return "redirect:/admin/products";
    }

    // Hàm tiện ích để lưu ảnh
    private void saveImages(Product product,
                            MultipartFile image,
                            MultipartFile image2,
                            MultipartFile image3,
                            MultipartFile imageSizeGuide) throws IOException {

        // Ảnh chính
        if (!image.isEmpty()) {
            String fileName = image.getOriginalFilename();
            String filePath = servletContext.getRealPath("/images/" + fileName);
            createFileIfNotExist(filePath);
            image.transferTo(new File(filePath));
            product.setImage(fileName);
        }

        // Ảnh phụ 1
        if (!image2.isEmpty()) {
            String fileName2 = image2.getOriginalFilename();
            String filePath2 = servletContext.getRealPath("/images/" + fileName2);
            createFileIfNotExist(filePath2);
            image2.transferTo(new File(filePath2));
            product.setImage2(fileName2);
        }

        // Ảnh phụ 2
        if (!image3.isEmpty()) {
            String fileName3 = image3.getOriginalFilename();
            String filePath3 = servletContext.getRealPath("/images/" + fileName3);
            createFileIfNotExist(filePath3);
            image3.transferTo(new File(filePath3));
            product.setImage3(fileName3);
        }

        // Ảnh Size Guide
        if (!imageSizeGuide.isEmpty()) {
            String fileNameSG = imageSizeGuide.getOriginalFilename();
            String filePathSG = servletContext.getRealPath("/images/" + fileNameSG);
            createFileIfNotExist(filePathSG);
            imageSizeGuide.transferTo(new File(filePathSG));
            product.setSizeGuide(fileNameSG);
        }
    }

    // Tạo thư mục nếu chưa tồn tại
    private void createFileIfNotExist(String filePath) throws IOException {
        Path path = Path.of(filePath).getParent();
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }
}
