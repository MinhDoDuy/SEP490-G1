package com.ffood.g1.service;

import com.ffood.g1.entity.Food;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FoodService {
   //get food random in home
    List<Food> getRandomFood();

    //get all Food
    Page<Food> getAllFood(Pageable pageable);

    //get food by category
    List<Food> getFoodsByCategory(Integer categoryId);
}
