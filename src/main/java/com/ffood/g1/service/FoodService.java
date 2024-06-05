package com.ffood.g1.service;

import com.ffood.g1.entity.Food;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;


public interface FoodService {
   //get food random in home
    List<Food> getRandomFood();

    //get all Food
    Page<Food> getAllFood(Pageable pageable);

    //get food by category
    List<Food> getFoodsByCategory(Integer categoryId);


 Page<Food> getFoodByCategoryAndName(Integer categoryId, String name, Pageable pageable);

 Page<Food> getFoodByName(String name, Pageable pageable);

 Page<Food> getFoodByCategory(Integer categoryId, Pageable pageable);

 Optional<Food> getFoodById(Integer id) ;

 
}
