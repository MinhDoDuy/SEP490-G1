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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FoodServiceImplTest {

    @InjectMocks
    private FoodServiceImpl foodService;

    @Mock
    private FoodRepository foodRepository;

    @Test
    void testGetRandomFood() {
        Pageable limit = PageRequest.of(0, 12);
        List<Food> expectedFoods = Arrays.asList(new Food(), new Food());

        when(foodRepository.findRandomItems(limit)).thenReturn(expectedFoods);

        List<Food> result = foodService.getRandomFood();

        assertEquals(expectedFoods, result);
        verify(foodRepository, times(1)).findRandomItems(limit);
    }

    @Test
    void testGetAllFood() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Food> expectedPage = new PageImpl<>(Collections.emptyList());

        when(foodRepository.findAll(pageable)).thenReturn(expectedPage);

        Page<Food> result = foodService.getAllFood(pageable);

        assertEquals(expectedPage, result);
        verify(foodRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetFoodsByCategory() {
        Integer categoryId = 1;
        Category category = new Category();
        category.setCategoryId(categoryId);
        List<Food> expectedFoods = Arrays.asList(new Food(), new Food());

        when(foodRepository.findByCategory(category)).thenReturn(expectedFoods);

        List<Food> result = foodService.getFoodsByCategory(categoryId);

        assertEquals(expectedFoods, result);
        verify(foodRepository, times(1)).findByCategory(category);
    }

    @Test
    void testGetFoodsByCategoryId() {
        Integer categoryId = 1;
        List<Food> expectedFoods = Arrays.asList(new Food(), new Food());

        when(foodRepository.getFoodsByCategoryId(categoryId)).thenReturn(expectedFoods);

        List<Food> result = foodService.getFoodsByCategoryId(categoryId);

        assertEquals(expectedFoods, result);
        verify(foodRepository, times(1)).getFoodsByCategoryId(categoryId);
    }

    @Test
    void testGetFoodsByCanteenId() {
        Integer canteenId = 1;
        List<Food> expectedFoods = Arrays.asList(new Food(), new Food());

        when(foodRepository.findByCanteenId(canteenId)).thenReturn(expectedFoods);

        List<Food> result = foodService.getFoodsByCanteenId(canteenId);

        assertEquals(expectedFoods, result);
        verify(foodRepository, times(1)).findByCanteenId(canteenId);
    }

    @Test
    void testCountFoodsByCanteenId() {
        Integer canteenId = 1;
        int expectedCount = 10;

        when(foodRepository.countByCanteenId(canteenId)).thenReturn(expectedCount);

        int result = foodService.countFoodsByCanteenId(canteenId);

        assertEquals(expectedCount, result);
        verify(foodRepository, times(1)).countByCanteenId(canteenId);
    }

    @Test
    void testGetFoodById() {
        Integer foodId = 1;
        Food expectedFood = new Food();

        when(foodRepository.findById(foodId)).thenReturn(Optional.of(expectedFood));

        Optional<Food> result = foodService.getFoodById(foodId);

        assertTrue(result.isPresent());
        assertEquals(expectedFood, result.get());
        verify(foodRepository, times(1)).findById(foodId);
    }

    @Test
    void testSave() {
        Food food = new Food();

        foodService.save(food);

        verify(foodRepository, times(1)).save(food);
    }

    @Test
    void testGetSaleCountByCanteenId() {
        Integer canteenId = 1;
        int expectedSaleCount = 100;

        when(foodRepository.getSaleCountByCanteenId(canteenId)).thenReturn(expectedSaleCount);

        int result = foodService.getSaleCountByCanteenId(canteenId);

        assertEquals(expectedSaleCount, result);
        verify(foodRepository, times(1)).getSaleCountByCanteenId(canteenId);
    }

    @Test
    void testGetFilteredFoods() {
        List<Integer> categoryIds = Arrays.asList(1, 2);
        List<Integer> canteenIds = Arrays.asList(1, 2);
        String name = "test";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Food> expectedPage = new PageImpl<>(Collections.emptyList());

        when(foodRepository.findByCategoriesAndCanteensAndName(categoryIds, canteenIds, name, pageable)).thenReturn(expectedPage);

        Page<Food> result = foodService.getFilteredFoods(categoryIds, canteenIds, name, pageable);

        assertEquals(expectedPage, result);
        verify(foodRepository, times(1)).findByCategoriesAndCanteensAndName(categoryIds, canteenIds, name, pageable);
    }
}
