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
@Table(name = "canteens")
public class Canteen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "canteen_id")
    private Integer canteenId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "canteen_name", nullable = false)
    private String canteenName;

    @Column(name = "location")
    private String location;

    @Column(name = "canteen_phone")
    private String canteenPhone;

    @Column(name = "opening_hours")
    private String openingHours;

    @OneToMany(mappedBy = "canteen", cascade = CascadeType.ALL)
    private Set<Food> foods = Collections.emptySet(); // Initialize as empty set
}
