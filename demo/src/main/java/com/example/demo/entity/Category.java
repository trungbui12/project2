package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     Integer id;

    @Column(columnDefinition = "nvarchar(60)")
    @NotBlank(message = "Chua nhap ten loai san pham")
     String name;
    @NotBlank(message = "Chua nhap slug")
     String slug;
    Integer parentId;
    Boolean active;

    @OneToMany(mappedBy = "category")
     List<Product> products;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public @NotBlank(message = "Chua nhap ten loai san pham") String getName() {
        return name;
    }

    public void setName(@NotBlank(message = "Chua nhap ten loai san pham") String name) {
        this.name = name;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public @NotBlank(message = "Chua nhap slug") String getSlug() {
        return slug;
    }

    public void setSlug(@NotBlank(message = "Chua nhap slug") String slug) {
        this.slug = slug;
    }
}
