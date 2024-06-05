package com.ffood.g1.repository;




import com.ffood.g1.entity.Category;
import com.ffood.g1.entity.Food;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FoodRepository extends PagingAndSortingRepository<Food, Integer> {
    @Query("SELECT i FROM Food i ORDER BY RAND()")
    List<Food> findRandomItems(Pageable pageable);

    List<Food> findByCategory(Category category);

    Page<Food> findByCategoryCategoryId(Integer categoryId, Pageable pageable);

    Page<Food> findByFoodNameContaining(String name, Pageable pageable);

    Page<Food> findByCategoryCategoryIdAndFoodNameContaining(Integer categoryId, String name, Pageable pageable);

    Optional<Food> findById(Integer id);
    //Page<Item> findAll(Pageable pageable);
}
