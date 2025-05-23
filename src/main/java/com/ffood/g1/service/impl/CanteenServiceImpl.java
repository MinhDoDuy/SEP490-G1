package com.ffood.g1.service.impl;

import com.ffood.g1.entity.Canteen;
import com.ffood.g1.entity.User;
import com.ffood.g1.repository.CanteenRepository;
import com.ffood.g1.service.CanteenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CanteenServiceImpl implements CanteenService {
    @Autowired
    private CanteenRepository canteenRepository;

    @Override
    public List<Canteen> getAllCanteens() {
        return canteenRepository.findAll();
    }

    @Override
    public Page<Canteen> getAllCanteensPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return canteenRepository.findAll(pageable);
    }


    @Override
    public void saveCanteen(Canteen canteen) {
        canteenRepository.save(canteen);
    }

    @Override
    public Page<Canteen> searchCanteens(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return canteenRepository.findByCanteenNameContainingIgnoreCase(
                keyword, pageable);
    }


    @Override
    public Canteen getCanteenById(Integer canteenId) {
        return canteenRepository.findById(canteenId).orElse(null);
    }

    @Override
    public void updateCanteen(Canteen canteen) {
        canteenRepository.save(canteen);

    }

    @Override
    public Integer countCanteens() {
        return Math.toIntExact(canteenRepository.count());
    }

    @Override

    public Canteen loadCanteenId(Integer canteenId) {
        return canteenRepository.findById(canteenId).orElse(null);
    }

    public boolean isPhoneExist(String phone) {
        return canteenRepository.findByCanteenPhone(phone) != null;
    }

    @Override
    public boolean isCanteenNameExist(String canteenName) {
        return canteenRepository.findByCanteenName(canteenName) != null;
    }


}
