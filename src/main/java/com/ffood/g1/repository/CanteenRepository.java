package com.ffood.g1.repository;

import com.ffood.g1.entity.Canteen;

import com.ffood.g1.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CanteenRepository extends JpaRepository<Canteen, Integer> {


    Page<Canteen> findByCanteenNameContainingIgnoreCaseOrLocationContainingIgnoreCaseOrCanteenPhoneContainingIgnoreCase(
            String canteenName, String location, String canteenPhone, Pageable pageable);


}