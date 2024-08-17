package com.ffood.g1.service.impl;

import com.ffood.g1.entity.Food;
import com.ffood.g1.repository.FoodRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FoodServiceImplTest {

    @InjectMocks
    private FoodServiceImpl foodService;

    @Mock
    private FoodRepository foodRepository;

    // Trường hợp bình thường: Kiểm thử lấy các món ăn ngẫu nhiên
    @Test
    void testGetRandomFood_Normal() {
        // Tạo Pageable giả lập
        Pageable limit = PageRequest.of(0, 12);

        // Tạo các đối tượng Food
        Food food1 = Food.builder().foodId(1).foodName("Food1").foodStatusActive(true).build();
        Food food2 = Food.builder().foodId(2).foodName("Food2").foodStatusActive(true).build();

        // Giả lập phương thức findRandomItems của repository để trả về danh sách các món ăn
        when(foodRepository.findRandomItems(limit)).thenReturn(Arrays.asList(food1, food2));

        // Gọi phương thức service
        List<Food> result = foodService.getRandomFood();

        // Kiểm tra xem danh sách trả về có khớp với danh sách mong đợi hay không
        assertEquals(Arrays.asList(food1, food2), result);

        // Xác minh rằng phương thức findRandomItems của repository đã được gọi chính xác một lần
        verify(foodRepository, times(1)).findRandomItems(limit);
    }

    // Trường hợp bình thường: Kiểm thử lấy tất cả các món ăn với phân trang
    @Test
    void testGetAllFood_Normal() {
        Pageable pageable = PageRequest.of(0, 10);

        Food food1 = Food.builder().foodId(1).foodName("Food1").build();
        Food food2 = Food.builder().foodId(2).foodName("Food2").build();

        // Tạo expectedPage trực tiếp
        Page<Food> expectedPage = new PageImpl<>(Arrays.asList(food1, food2), pageable, 2);

        // Mock repository để trả về expectedPage
        when(foodRepository.findAll(pageable)).thenReturn(expectedPage);

        // Gọi phương thức cần kiểm tra
        Page<Food> result = foodService.getAllFood(pageable);

        // Kiểm tra xem kết quả có giống với expectedPage không
        assertEquals(expectedPage, result);

        // Xác minh rằng phương thức của repository đã được gọi một lần
        verify(foodRepository, times(1)).findAll(pageable);
    }


    // Trường hợp bình thường: Kiểm thử lấy món ăn theo ID
    @Test
    void testGetFoodById_Normal() {
        Food food = Food.builder().foodId(1).foodName("Food1").build();

        when(foodRepository.findById(1)).thenReturn(Optional.of(food));

        Optional<Food> result = foodService.getFoodById(1);

        assertTrue(result.isPresent());
        assertEquals(food, result.get());

        verify(foodRepository, times(1)).findById(1);
    }

    // Trường hợp bình thường: Kiểm thử đếm số lượng món ăn theo CanteenId
    @Test
    void testCountFoodsByCanteenId_Normal() {
        when(foodRepository.countByCanteenId(1)).thenReturn(10);

        Integer result = foodService.countFoodsByCanteenId(1);

        assertEquals(10, result);

        verify(foodRepository, times(1)).countByCanteenId(1);
    }

    // Trường hợp bình thường: Kiểm thử tìm kiếm món ăn theo từ khóa
    @Test
    void testSearchFoods_Normal() {
        Pageable pageable = PageRequest.of(0, 10);

        Food food1 = Food.builder().foodId(1).foodName("Pizza").build();
        Food food2 = Food.builder().foodId(2).foodName("Pasta").build();

        Page<Food> page = new PageImpl<>(Arrays.asList(food1, food2), pageable, 2);

        when(foodRepository.searchFoods("P", null, 1, pageable)).thenReturn(page);

        Page<Food> result = foodService.searchFoods("P", null, 1, pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());

        verify(foodRepository, times(1)).searchFoods("P", null, 1, pageable);
    }

    // Trường hợp bình thường: Kiểm thử lấy tổng số lượng bán theo CanteenId
    @Test
    void testGetSaleCountByCanteenId_Normal() {
        when(foodRepository.getSaleCountByCanteenId(1)).thenReturn(100);

        Integer result = foodService.getSaleCountByCanteenId(1);

        assertEquals(100, result);

        verify(foodRepository, times(1)).getSaleCountByCanteenId(1);
    }

    // Trường hợp bình thường: Kiểm thử lọc món ăn theo nhiều điều kiện
    @Test
    void testGetFilteredFoods_Normal() {
        Pageable pageable = PageRequest.of(0, 10);

        Food food1 = Food.builder().foodId(1).foodName("Pizza").build();
        Food food2 = Food.builder().foodId(2).foodName("Pasta").build();

        Page<Food> page = new PageImpl<>(Arrays.asList(food1, food2), pageable, 2);

        when(foodRepository.findByCategoriesAndCanteensAndName(Arrays.asList(1), Arrays.asList(1), "P", pageable)).thenReturn(page);

        Page<Food> result = foodService.getFilteredFoods(Arrays.asList(1), Arrays.asList(1), "P", pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());

        verify(foodRepository, times(1)).findByCategoriesAndCanteensAndName(Arrays.asList(1), Arrays.asList(1), "P", pageable);
    }
}
