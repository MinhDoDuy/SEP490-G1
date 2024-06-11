package com.ffood.g1.service;

import com.ffood.g1.entity.Canteen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CanteenService {
    List<Canteen> getAllCanteens();

    Page<Canteen> getAllCanteensPage(Pageable pageable);
    void saveCanteen(Canteen canteen);
}
