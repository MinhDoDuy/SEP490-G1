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
    private Integer canteenId;

    @Column(name = "canteen_name", nullable = false)
    private String canteenName;

    @Column(name = "location")
    private String location;

    @Column(name = "canteen_phone")
    private String canteenPhone;

    @Column(name = "opening_hours")
    private String openingHours;

    @Column(name = "opening_day")
    private String openingDay;

    @Column(name = "canteen_img")
    private String canteenImg;

    @Column(name = "is_active")
    private Boolean isActive = true;


}