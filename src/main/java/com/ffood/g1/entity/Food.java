package com.ffood.g1.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Collections;
import java.util.Set;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "food")
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "food_id")
    private Integer foodId;

    @ManyToOne
    @JoinColumn(name = "canteen_id", nullable = false)
    private Canteen canteen;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "food_name", nullable = false)
    private String foodName;

    @Column(name = "description")
    private String description;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "sales_count")
    private Integer salesCount;

    @Column(name = "image_food")
    private String imageFood;

    @Column(name = "image_food_detail1")
    private String imageFoodDetail1;

    @Column(name = "image_food_detail2")
    private String imageFoodDetail2;

    public Food(Integer foodId) {
    }

//    @OneToMany(mappedBy = "food", cascade = CascadeType.ALL)
//    private Set<OrderDetail> orderDetails = Collections.emptySet(); // Initialize as empty set
}
