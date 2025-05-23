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

    Optional<Food> getFoodById(Integer id);

    Page<Food> getFilteredFoods(List<Integer> checkedCategories, List<Integer> checkedCanteens, String name, Pageable pageable);

    Optional<Food> getFoodByIdFoodDetails(Integer id);


    List<Food> findByCanteenId(Integer canteenId);

    void save(Food food);

    List<Food> getFoodsByCategoryId(Integer categoryId);

    List<Food> getFoodsByCanteenId(Integer canteenId);

    Integer countFoodsByCanteenId(Integer canteenId);

    Integer countFoodByCanteenId(Integer canteenId);


    Page<Food> findFoodByCanteenId(Integer canteenId, Pageable pageable);

    Page<Food> searchFoods(String keyword, Integer categoryId, Integer canteenId, Pageable pageable);

    Integer getSaleCountByCanteenId(Integer canteenId);
}
