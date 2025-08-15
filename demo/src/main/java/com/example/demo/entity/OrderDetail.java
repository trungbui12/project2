package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "order_details")
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    int quantity;
    int price;
    @ManyToOne
    @JoinColumn(name = "order_id")
    Order order;
    @ManyToOne @JoinColumn(name = "product_id")
    Product product;
}

