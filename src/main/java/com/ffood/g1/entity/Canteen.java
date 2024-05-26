package com.ffood.g1.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "canteen")
public class Canteen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "canteen_id")
    private Long canteenId;

    @Column(name = "canteen_name", nullable = false)
    private String canteenName;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "canteen_phone")
    private String canteenPhone;

    @Column(name = "opening_hours")
    private String openingHours;


    @Column(name = "canteen_img", columnDefinition = "VARCHAR(500)")
    private String canteenImg;

    @ManyToOne
    @JoinColumn(name = "manager_id", referencedColumnName = "user_id")
    private User manager;



}
