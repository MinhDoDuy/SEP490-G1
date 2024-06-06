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




    public Page<Food> getFoodByCategory(Integer categoryId, Pageable pageable) {
        return foodRepository.findByCategoryCategoryId(categoryId, pageable);
    }

    public Page<Food> getFoodByName(String name, Pageable pageable) {
        return foodRepository.findByFoodNameContaining(name, pageable);
    }

    public Page<Food> getFoodByCategoryAndName(Integer categoryId, String name, Pageable pageable) {
        return foodRepository.findByCategoryCategoryIdAndFoodNameContaining(categoryId, name, pageable);
    }

    public Optional<Food> getFoodById(Integer id) {
        return foodRepository.findById(id);
    }

}