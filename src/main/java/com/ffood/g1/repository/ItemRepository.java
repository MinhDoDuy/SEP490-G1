package com.ffood.g1.repository;




import com.ffood.g1.entity.Food;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ItemRepository extends PagingAndSortingRepository<Food, Long> {
    @Query("SELECT i FROM Food i ORDER BY RAND()")
    List<Food> findRandomItems(Pageable pageable);


    //Page<Item> findAll(Pageable pageable);
}
