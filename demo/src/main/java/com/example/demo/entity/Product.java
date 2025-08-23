package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     Integer id;

    @Column(columnDefinition = "nvarchar(60)")
    @NotBlank(message = "chua nhap ten san pham")
     String name;
     String slug;
    @Column(columnDefinition = "nvarchar(max)")
     String description;
    @Column(columnDefinition = "nvarchar(255)")
    String image;
    @NotNull(message = "chua nhap gia san pham")
    @Positive(message = "Gia phai lon hon 0")
     Integer price;
    @NotNull(message = "chua nhap gia san pham")
    @Positive(message = "Gia phai lon hon 0")
     Integer quantity;
     Boolean active;
    @Column(columnDefinition = "varchar(255)")
     String sizes; // mới thêm

    @Column(columnDefinition = "varchar(255)")
     String colors; // mới thêm
    @ManyToOne @JoinColumn(name = "category_id")
    Category category;
    @OneToMany(mappedBy = "product")
    List<Favorite> favorites;
    @OneToMany(mappedBy = "product")
    List<CartDetail> cartDetails;

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<CartDetail> getCartDetails() {
        return cartDetails;
    }

    public void setCartDetails(List<CartDetail> cartDetails) {
        this.cartDetails = cartDetails;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Favorite> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<Favorite> favorites) {
        this.favorites = favorites;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public @NotBlank(message = "chua nhap ten san pham") String getName() {
        return name;
    }

    public void setName(@NotBlank(message = "chua nhap ten san pham") String name) {
        this.name = name;
    }

    public @NotNull(message = "chua nhap gia san pham") @Positive(message = "Gia phai lon hon 0") Integer getPrice() {
        return price;
    }

    public void setPrice(@NotNull(message = "chua nhap gia san pham") @Positive(message = "Gia phai lon hon 0") Integer price) {
        this.price = price;
    }

    public @NotNull(message = "chua nhap gia san pham") @Positive(message = "Gia phai lon hon 0") Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(@NotNull(message = "chua nhap gia san pham") @Positive(message = "Gia phai lon hon 0") Integer quantity) {
        this.quantity = quantity;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getColors() {
        return colors;
    }

    public void setColors(String colors) {
        this.colors = colors;
    }

    public String getSizes() {
        return sizes;
    }

    public void setSizes(String sizes) {
        this.sizes = sizes;
    }
}
