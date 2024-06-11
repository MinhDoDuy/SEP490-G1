package com.ffood.g1.service;

import com.ffood.g1.entity.Canteen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CanteenService {
    List<Canteen> getAllCanteens();

    Page<Canteen> getAllCanteensPage(int page, int size);

    void saveCanteen(Canteen canteen);

    Page<Canteen> searchCanteens(String keyword, int page, int size);
}
