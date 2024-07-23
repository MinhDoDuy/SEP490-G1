package com.ffood.g1.repository;

import com.ffood.g1.entity.Canteen;

import com.ffood.g1.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CanteenRepository extends JpaRepository<Canteen, Integer> {

    Canteen findByCanteenPhone(String phone);
    Canteen findByCanteenName(String canteenName);
    Page<Canteen> findByCanteenNameContainingIgnoreCaseOrLocationContainingIgnoreCaseOrCanteenPhoneContainingIgnoreCase(
    String canteenName, String location, String canteenPhone, Pageable pageable);

}

