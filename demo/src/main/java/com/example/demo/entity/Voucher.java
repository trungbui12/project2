package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "vouchers")
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    @NotBlank(message =  "Chưa nhập mã giảm giá")
    String code;
    @NotNull(message = "Chưa nhập % giảm giá")
    @Range(min = 1, max = 50, message = "Giá trị giảm giá chỉ từ 1 - 50%")
    Integer discountPercent;
    @NotNull(message = "Chưa nhập % số lượng")
    @Positive(message = "Số lượng phải > 0")
    Integer quantity;
    @Temporal(TemporalType.DATE)
    Date createdAt;
    @Temporal(TemporalType.DATE)
    @NotNull(message = "Chưa nhập ngày bắt đầu")
    Date startedAt;
    @Temporal(TemporalType.DATE)
    @NotNull(message = "Chưa nhập ngày kết thúc")
    Date endAt;
    Boolean actived;
    @OneToMany(mappedBy = "voucher")
    List<Order> orders;

    public Boolean getActived() {
        return actived;
    }

    public void setActived(Boolean actived) {
        this.actived = actived;
    }

    public @NotBlank(message = "Chưa nhập mã giảm giá") String getCode() {
        return code;
    }

    public void setCode(@NotBlank(message = "Chưa nhập mã giảm giá") String code) {
        this.code = code;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public @NotNull(message = "Chưa nhập % giảm giá") @Range(min = 1, max = 50, message = "Giá trị giảm giá chỉ từ 1 - 50%") Integer getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(@NotNull(message = "Chưa nhập % giảm giá") @Range(min = 1, max = 50, message = "Giá trị giảm giá chỉ từ 1 - 50%") Integer discountPercent) {
        this.discountPercent = discountPercent;
    }

    public @NotNull(message = "Chưa nhập ngày kết thúc") Date getEndAt() {
        return endAt;
    }

    public void setEndAt(@NotNull(message = "Chưa nhập ngày kết thúc") Date endAt) {
        this.endAt = endAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public @NotNull(message = "Chưa nhập % số lượng") @Positive(message = "Số lượng phải > 0") Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(@NotNull(message = "Chưa nhập % số lượng") @Positive(message = "Số lượng phải > 0") Integer quantity) {
        this.quantity = quantity;
    }

    public @NotNull(message = "Chưa nhập ngày bắt đầu") Date getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(@NotNull(message = "Chưa nhập ngày bắt đầu") Date startedAt) {
        this.startedAt = startedAt;
    }
}
