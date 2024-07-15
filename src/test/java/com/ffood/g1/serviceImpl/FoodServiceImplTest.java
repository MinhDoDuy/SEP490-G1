package com.ffood.g1.serviceImpl;

import com.ffood.g1.entity.Category;
import com.ffood.g1.entity.Food;
import com.ffood.g1.repository.FoodRepository;
import com.ffood.g1.service.impl.FoodServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FoodServiceImplTest {

    @InjectMocks
    private FoodServiceImpl service;

    @Mock
    private FoodRepository repository;

    @Test
    void getRandomFood() {
        Pageable pageable = PageRequest.of(0, 12);
        List<Food> foods = Arrays.asList(new Food(), new Food());
        when(repository.findRandomItems(pageable)).thenReturn(foods);

        List<Food> result = service.getRandomFood();

        assertEquals(2, result.size());
        verify(repository, times(1)).findRandomItems(pageable);
    }

    @Test
    void getAllFood() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Food> foods = Arrays.asList(new Food(), new Food());
        Page<Food> foodPage = new PageImpl<>(foods);
        when(repository.findAll(pageable)).thenReturn(foodPage);

        Page<Food> result = service.getAllFood(pageable);

        assertEquals(2, result.getTotalElements());
        verify(repository, times(1)).findAll(pageable);
    }

    @Test
    void getFoodsByCategory() {
        Integer categoryId = 1;
        Category category = new Category();
        category.setCategoryId(categoryId);
        List<Food> foods = Arrays.asList(new Food(), new Food());
        when(repository.findByCategory(category)).thenReturn(foods);

        List<Food> result = service.getFoodsByCategory(categoryId);

        assertEquals(2, result.size());
        verify(repository, times(1)).findByCategory(category);
    }

    @Test
    void getFoodById() {
        Integer foodId = 1;
        Food food = new Food();
        when(repository.findById(foodId)).thenReturn(Optional.of(food));

        Optional<Food> result = service.getFoodById(foodId);

        assertTrue(result.isPresent());
        assertEquals(food, result.get());
        verify(repository, times(1)).findById(foodId);
    }

    @Test
    void getFoodByIdFoodDetails() {
        Integer foodId = 1;
        Food food = new Food();
        when(repository.findById(foodId)).thenReturn(Optional.of(food));

        Optional<Food> result = service.getFoodByIdFoodDetails(foodId);

        assertTrue(result.isPresent());
        assertEquals(food, result.get());
        verify(repository, times(1)).findById(foodId);
    }

    @Test
    void findByCanteenId() {
        Integer canteenId = 1;
        List<Food> foods = Arrays.asList(new Food(), new Food());
        when(repository.findByCanteenId(canteenId)).thenReturn(foods);

        List<Food> result = service.findByCanteenId(canteenId);

        assertEquals(2, result.size());
        verify(repository, times(1)).findByCanteenId(canteenId);
    }

    @Test
    void save() {
        Food food = new Food();
        service.save(food);
        verify(repository, times(1)).save(food);
    }

    @Test
    void countFoodByCanteenId() {
        Integer canteenId = 1;
        Integer count = 10;
        when(repository.countFoodByCanteenId(canteenId)).thenReturn(count);

        Integer result = service.countFoodByCanteenId(canteenId);

        assertEquals(count, result);
        verify(repository, times(1)).countFoodByCanteenId(canteenId);
    }

    @Test
    void getFilteredFoods() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Food> foods = Arrays.asList(new Food(), new Food());
        Page<Food> foodPage = new PageImpl<>(foods);

        List<Integer> categoryIds = Arrays.asList(1, 2);
        List<Integer> canteenIds = Arrays.asList(1, 2);
        String name = "test";

        when(repository.findByCategoriesAndCanteensAndName(categoryIds, canteenIds, name, pageable)).thenReturn(foodPage);
        when(repository.findByCategoriesAndCanteens(categoryIds, canteenIds, pageable)).thenReturn(foodPage);
        when(repository.findByCategoriesAndName(categoryIds, name, pageable)).thenReturn(foodPage);
        when(repository.findByCanteensAndName(canteenIds, name, pageable)).thenReturn(foodPage);
        when(repository.findByCategoryIds(categoryIds, pageable)).thenReturn(foodPage);
        when(repository.findByCanteenIds(canteenIds, pageable)).thenReturn(foodPage);
        when(repository.findByNameContainingIgnoreCase(name, pageable)).thenReturn(foodPage);
        when(repository.findAll(pageable)).thenReturn(foodPage);

        // Test with all filters
        Page<Food> result = service.getFilteredFoods(categoryIds, canteenIds, name, pageable);
        assertEquals(2, result.getTotalElements());
        verify(repository, times(1)).findByCategoriesAndCanteensAndName(categoryIds, canteenIds, name, pageable);

        // Test with categoryIds and canteenIds
        result = service.getFilteredFoods(categoryIds, canteenIds, null, pageable);
        assertEquals(2, result.getTotalElements());
        verify(repository, times(1)).findByCategoriesAndCanteens(categoryIds, canteenIds, pageable);

        // Test with categoryIds and name
        result = service.getFilteredFoods(categoryIds, null, name, pageable);
        assertEquals(2, result.getTotalElements());
        verify(repository, times(1)).findByCategoriesAndName(categoryIds, name, pageable);

        // Test with canteenIds and name
        result = service.getFilteredFoods(null, canteenIds, name, pageable);
        assertEquals(2, result.getTotalElements());
        verify(repository, times(1)).findByCanteensAndName(canteenIds, name, pageable);

        // Test with categoryIds only
        result = service.getFilteredFoods(categoryIds, null, null, pageable);
        assertEquals(2, result.getTotalElements());
        verify(repository, times(1)).findByCategoryIds(categoryIds, pageable);

        // Test with canteenIds only
        result = service.getFilteredFoods(null, canteenIds, null, pageable);
        assertEquals(2, result.getTotalElements());
        verify(repository, times(1)).findByCanteenIds(canteenIds, pageable);

        // Test with name only
        result = service.getFilteredFoods(null, null, name, pageable);
        assertEquals(2, result.getTotalElements());
        verify(repository, times(1)).findByNameContainingIgnoreCase(name, pageable);

        // Test with no filters
        result = service.getFilteredFoods(null, null, null, pageable);
        assertEquals(2, result.getTotalElements());
        verify(repository, times(1)).findAll(pageable);
    }
}
