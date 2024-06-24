package com.ffood.g1.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Integer cartId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    @Column(name = "status")
    private String status; // e.g., active, completed, cancelled

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

//    @OneToMany
//    @JoinColumn(name = "user_id")
//    private User user;

//    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true )
//    private Set<CartItem> cartItems = new HashSet<>(); // Initialize as empty set

    @Override
    public String toString() {
        return "Cart{" +
                "id=" + cartId +
                ", user=" + user +
                ", transactionDate=" + transactionDate +
                ", totalAmount=" + totalAmount +
                '}';
    }

}
