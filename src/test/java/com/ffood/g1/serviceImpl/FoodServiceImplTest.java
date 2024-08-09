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

    // Trường hợp bình thường: Kiểm thử lấy các món ăn ngẫu nhiên
    @Test
    void testGetRandomFood_Normal() {
        // Tạo Pageable giả lập
        Pageable limit = PageRequest.of(0, 12);

        // Tạo các đối tượng Food sử dụng builder
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

    // Trường hợp bất thường: Kiểm thử cách xử lý khi repository ném ra ngoại lệ
    @Test
    void testGetRandomFood_Abnormal() {
        Pageable limit = PageRequest.of(0, 12);

        // Giả lập repository ném ra một RuntimeException khi gọi findRandomItems
        when(foodRepository.findRandomItems(limit)).thenThrow(new RuntimeException("Database Error"));

        // Gọi phương thức service và kiểm tra xem có ngoại lệ nào bị ném ra hay không
        Exception exception = assertThrows(RuntimeException.class, () -> {
            foodService.getRandomFood();
        });

        // Kiểm tra xem thông điệp ngoại lệ có khớp với thông điệp mong đợi hay không
        assertEquals("Database Error", exception.getMessage());

        // Xác minh rằng phương thức findRandomItems của repository đã được gọi chính xác một lần
        verify(foodRepository, times(1)).findRandomItems(limit);
    }

    // Trường hợp ranh giới: Kiểm thử khi không có món ăn nào được trả về
    @Test
    void testGetRandomFood_Boundary() {
        Pageable limit = PageRequest.of(0, 12);

        // Giả lập phương thức findRandomItems của repository để trả về danh sách rỗng
        when(foodRepository.findRandomItems(limit)).thenReturn(Collections.emptyList());

        // Gọi phương thức service
        List<Food> result = foodService.getRandomFood();

        // Kiểm tra xem danh sách trả về có rỗng hay không
        assertTrue(result.isEmpty());

        // Xác minh rằng phương thức findRandomItems của repository đã được gọi chính xác một lần
        verify(foodRepository, times(1)).findRandomItems(limit);
    }

    // Trường hợp bình thường: Kiểm thử lấy tất cả các món ăn với phân trang
    @Test
    void testGetAllFood_Normal() {
        Pageable pageable = PageRequest.of(0, 10);

        // Tạo các đối tượng Food sử dụng builder
        Food food1 = Food.builder().foodId(1).foodName("Food1").foodStatusActive(true).build();
        Food food2 = Food.builder().foodId(2).foodName("Food2").foodStatusActive(true).build();

        Page<Food> expectedPage = new PageImpl<>(Arrays.asList(food1, food2));

        // Giả lập phương thức findAll của repository để trả về trang giả lập
        when(foodRepository.findAll(pageable)).thenReturn(expectedPage);

        // Gọi phương thức service
        Page<Food> result = foodService.getAllFood(pageable);

        // Kiểm tra xem trang trả về có khớp với trang mong đợi hay không
        assertEquals(expectedPage, result);

        // Xác minh rằng phương thức findAll của repository đã được gọi chính xác một lần
        verify(foodRepository, times(1)).findAll(pageable);
    }

    // Trường hợp bình thường: Kiểm thử lấy món ăn theo danh mục
    @Test
    void testGetFoodsByCategory_Normal() {
        Category category = Category.builder().categoryId(1).categoryName("Category1").build();

        // Tạo các đối tượng Food sử dụng builder
        Food food1 = Food.builder().foodId(1).foodName("Food1").category(category).foodStatusActive(true).build();
        Food food2 = Food.builder().foodId(2).foodName("Food2").category(category).foodStatusActive(true).build();

        // Giả lập phương thức findByCategory của repository để trả về danh sách món ăn
        when(foodRepository.findByCategory(category)).thenReturn(Arrays.asList(food1, food2));

        // Gọi phương thức service
        List<Food> result = foodService.getFoodsByCategory(1);

        // Kiểm tra xem danh sách trả về có khớp với danh sách mong đợi hay không
        assertEquals(Arrays.asList(food1, food2), result);

        // Xác minh rằng phương thức findByCategory của repository đã được gọi chính xác một lần
        verify(foodRepository, times(1)).findByCategory(category);
    }

    // Trường hợp bất thường: Kiểm thử khi repository ném ra ngoại lệ khi lấy món ăn theo danh mục
    @Test
    void testGetFoodsByCategory_Abnormal() {
        Category category = Category.builder().categoryId(1).categoryName("Category1").build();

        // Giả lập repository ném ra một RuntimeException khi gọi findByCategory
        when(foodRepository.findByCategory(category)).thenThrow(new RuntimeException("Database Error"));

        // Gọi phương thức service và kiểm tra xem có ngoại lệ nào bị ném ra hay không
        Exception exception = assertThrows(RuntimeException.class, () -> {
            foodService.getFoodsByCategory(1);
        });

        // Kiểm tra xem thông điệp ngoại lệ có khớp với thông điệp mong đợi hay không
        assertEquals("Database Error", exception.getMessage());

        // Xác minh rằng phương thức findByCategory của repository đã được gọi chính xác một lần
        verify(foodRepository, times(1)).findByCategory(category);
    }

    // Trường hợp ranh giới: Kiểm thử khi không có món ăn nào khớp với danh mục
    @Test
    void testGetFoodsByCategory_Boundary() {
        Category category = Category.builder().categoryId(1).categoryName("Category1").build();

        // Giả lập phương thức findByCategory của repository để trả về danh sách rỗng
        when(foodRepository.findByCategory(category)).thenReturn(Collections.emptyList());

        // Gọi phương thức service
        List<Food> result = foodService.getFoodsByCategory(1);

        // Kiểm tra xem danh sách trả về có rỗng hay không
        assertTrue(result.isEmpty());

        // Xác minh rằng phương thức findByCategory của repository đã được gọi chính xác một lần
        verify(foodRepository, times(1)).findByCategory(category);
    }

    // Các bài kiểm thử khác cũng tương tự như trên, sử dụng builder để tạo các đối tượng Food và kiểm tra các phương thức khác trong FoodServiceImpl

    // Ví dụ: Trường hợp bình thường: Kiểm thử việc lưu một món ăn
    @Test
    void testSaveFood_Normal() {
        // Tạo một đối tượng Food mới sử dụng builder
        Food food = Food.builder()
                .foodId(1)
                .foodName("Food1")
                .price(10000)
                .foodStatusActive(true)
                .build();

        // Gọi phương thức save của service
        foodService.save(food);

        // Xác minh rằng phương thức save của repository đã được gọi chính xác một lần
        verify(foodRepository, times(1)).save(food);
    }

    // Trường hợp bất thường: Kiểm thử cách xử lý khi repository ném ra ngoại lệ khi lưu món ăn
    @Test
    void testSaveFood_Abnormal() {
        // Tạo một đối tượng Food mới sử dụng builder
        Food food = Food.builder()
                .foodId(1)
                .foodName("Food1")
                .price(10000)
                .foodStatusActive(true)
                .build();

        // Giả lập repository ném ra một RuntimeException khi gọi save
        doThrow(new RuntimeException("Database Error")).when(foodRepository).save(food);

        // Gọi phương thức service và kiểm tra xem có ngoại lệ nào bị ném ra hay không
        Exception exception = assertThrows(RuntimeException.class, () -> {
            foodService.save(food);
        });

        // Kiểm tra xem thông điệp ngoại lệ có khớp với thông điệp mong đợi hay không
        assertEquals("Database Error", exception.getMessage());

        // Xác minh rằng phương thức save của repository đã được gọi chính xác một lần
        verify(foodRepository, times(1)).save(food);
    }

    // Trường hợp ranh giới: Kiểm thử việc lưu khi đối tượng food là null
    @Test
    void testSaveFood_Boundary() {
        // Đặt food bằng null để kiểm thử trường hợp ranh giới
        Food food = null;

        // Kiểm tra xem NullPointerException có bị ném ra hay không khi gọi phương thức save với giá trị null
        assertThrows(NullPointerException.class, () -> {
            foodService.save(food);
        });

        // Xác minh rằng phương thức save của repository không được gọi
        verify(foodRepository, times(0)).save(food);
    }
}
