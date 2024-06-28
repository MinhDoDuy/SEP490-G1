package com.ffood.g1.service;

import com.ffood.g1.entity.Canteen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
@Service

public interface CanteenService {
    List<Canteen> getAllCanteens();

    Page<Canteen> getAllCanteensPage(int page, int size);

    void saveCanteen(Canteen canteen);

    Page<Canteen> searchCanteens(String keyword, int page, int size);

    Canteen getCanteenById(Integer canteenId);

    void updateCanteen(Canteen canteen);

    Canteen loadCanteenId(Integer canteenId);
    Integer countCanteens();
    boolean isPhoneExist(String phone);
    boolean isCanteenNameExist(String canteenName);
}
