package com.ffood.g1.repository;




import com.ffood.g1.entity.Category;
import com.ffood.g1.entity.Food;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface FoodRepository extends PagingAndSortingRepository<Food, Long> {
    @Query("SELECT i FROM Food i ORDER BY RAND()")
    List<Food> findRandomItems(Pageable pageable);

    List<Food> findByCategory(Category category);

    //Page<Item> findAll(Pageable pageable);
}
