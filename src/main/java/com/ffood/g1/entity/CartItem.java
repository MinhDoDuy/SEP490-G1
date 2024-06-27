//package com.ffood.g1.entity;
//
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import javax.persistence.*;
//import java.time.LocalDateTime;
//
//@Entity
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//@Table(name = "cart_item")
//public class CartItem {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "cart_item")
//    private Integer cartItem;
//
//    @ManyToOne
//    @JoinColumn(name = "cart_id", nullable = false)
//    private Cart cart;
//
//    @ManyToOne
//    @JoinColumn(name = "food_id", nullable = false)
//    private Food food;
//
//    @Column(name = "amount", nullable = false)
//    private Integer amount;
//
//    @Column(name = "transaction_date")
//    private LocalDateTime transactionDate;
//}
