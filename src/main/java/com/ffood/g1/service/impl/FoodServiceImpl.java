package com.ffood.g1.service.impl;


import com.ffood.g1.entity.Category;
import com.ffood.g1.entity.Food;
import com.ffood.g1.repository.FoodRepository;
import com.ffood.g1.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoodServiceImpl implements FoodService {
    @Autowired
    private FoodRepository foodRepository;

    @Override
    public List<Food> getRandomFood() {
        Pageable limit = PageRequest.of(0, 12);
        return foodRepository.findRandomItems(limit);
    }

    @Override
    public Page<Food> getAllFood(Pageable pageable) {
        return foodRepository.findAll(pageable);
    }


    @Override
    public List<Food> getFoodsByCategory(Integer categoryId) {
        Category category = new Category();
        category.setCategoryId(categoryId);
        return foodRepository.findByCategory(category);
    }
}