package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "contacts")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(columnDefinition = "nvarchar(60)")
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    String email;

    @Column(columnDefinition = "nvarchar(max)")
    @NotBlank(message = "Nội dung liên hệ không được để trống")
    String message;

    @ManyToOne
    @JoinColumn(name = "account_id")
    Account account;

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public @NotBlank(message = "Email không được để trống") @Email(message = "Email không hợp lệ") String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank(message = "Email không được để trống") @Email(message = "Email không hợp lệ") String email) {
        this.email = email;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public @NotBlank(message = "Nội dung liên hệ không được để trống") String getMessage() {
        return message;
    }

    public void setMessage(@NotBlank(message = "Nội dung liên hệ không được để trống") String message) {
        this.message = message;
    }
}
