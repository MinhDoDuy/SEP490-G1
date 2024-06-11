package com.ffood.g1.service.impl;

import com.ffood.g1.entity.Canteen;
import com.ffood.g1.repository.CanteenRepository;
import com.ffood.g1.service.CanteenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
    public Page<Canteen> getAllCanteensPage(Pageable pageable) {
        return canteenRepository.findAll(pageable);
    }

    @Override
    public void saveCanteen(Canteen canteen) {

    }

    public List<Canteen> getAllCanteenContact() {
        return canteenRepository.findAll();
    }


}
