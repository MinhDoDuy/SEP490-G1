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
import java.util.Optional;

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

    public List<Food> getFoodsByCategoryId(Integer categoryId) {
        return foodRepository.getFoodsByCategoryId(categoryId);
    }

    @Override
    public List<Food> getFoodsByCanteenId(Integer canteenId) {
        return foodRepository.findByCanteenId(canteenId);
    }

    @Override
    public Integer countFoodsByCanteenId(Integer canteenId) {
        return foodRepository.countByCanteenId(canteenId);
    }

    @Override
    public Optional<Food> getFoodById(Integer id) {
        return foodRepository.findById(id);
    }


    @Override
    public Optional<Food> getFoodByIdFoodDetails(Integer id) {
        return foodRepository.findById(id);
    }

    @Override
    public List<Food> findByCanteenId(Integer canteenId) {
        return foodRepository.findByCanteenId(canteenId);
    }

    @Override
    public void save(Food food) {
        foodRepository.save(food);
    }

    public Page<Food> getFilteredFoods(List<Integer> categoryIds, List<Integer> canteenIds, String name, Pageable pageable) {
        if (categoryIds != null && !categoryIds.isEmpty() && canteenIds != null && !canteenIds.isEmpty() && name != null) {
            return foodRepository.findByCategoriesAndCanteensAndName(categoryIds, canteenIds, name, pageable);
        } else if (categoryIds != null && !categoryIds.isEmpty() && canteenIds != null && !canteenIds.isEmpty()) {
            return foodRepository.findByCategoriesAndCanteens(categoryIds, canteenIds, pageable);
        } else if (categoryIds != null && !categoryIds.isEmpty() && name != null) {
            return foodRepository.findByCategoriesAndName(categoryIds, name, pageable);
        } else if (canteenIds != null && !canteenIds.isEmpty() && name != null) {
            return foodRepository.findByCanteensAndName(canteenIds, name, pageable);
        } else if (categoryIds != null && !categoryIds.isEmpty()) {
            return foodRepository.findByCategoryIds(categoryIds, pageable);
        } else if (canteenIds != null && !canteenIds.isEmpty()) {
            return foodRepository.findByCanteenIds(canteenIds, pageable);
        } else if (name != null) {
            return foodRepository.findByNameContainingIgnoreCase(name, pageable);
        }
        return foodRepository.findAll(pageable);
    }

}