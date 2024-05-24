package com.ffood.g1.repository;




import com.ffood.g1.entity.Item;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.domain.Page;
import java.util.List;

public interface ItemRepository extends PagingAndSortingRepository<Item, Long> {
    @Query("SELECT i FROM Item i ORDER BY RAND()")
    List<Item> findRandomItems(Pageable pageable);


    //Page<Item> findAll(Pageable pageable);
}
