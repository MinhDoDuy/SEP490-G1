package com.ffood.g1.repository;


import com.ffood.g1.entity.Category;
import com.ffood.g1.entity.Food;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
@Repository
public interface FoodRepository extends PagingAndSortingRepository<Food, Integer> {


    @Transactional
    @Modifying
    //Modifying annotation để giúp JPA đang thực hiện thao tác delete
    @Query("DELETE FROM Food f WHERE f.canteen.canteenId = :canteenId")
    void deleteByCanteenId(@Param("canteenId") Integer canteenId);


    // @Query("SELECT i FROM Food i ORDER BY RAND()")
    @Query("SELECT f FROM Food f ORDER BY f.salesCount DESC")
    List<Food> findRandomItems(Pageable pageable);

    List<Food> findByCategory(Category category);


    Optional<Food> findById(Integer id);
    //Page<Item> findAll(Pageable pageable);


    @Query("SELECT f FROM Food f JOIN f.category c JOIN f.canteen cn WHERE c.categoryId IN :categoryIds AND cn.canteenId IN :canteenIds AND LOWER(f.foodName) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Food> findByCategoriesAndCanteensAndName(@Param("categoryIds") List<Integer> categoryIds, @Param("canteenIds") List<Integer> canteenIds, @Param("name") String name, Pageable pageable);

    @Query("SELECT f FROM Food f JOIN f.category c JOIN f.canteen cn WHERE c.categoryId IN :categoryIds AND cn.canteenId IN :canteenIds")
    Page<Food> findByCategoriesAndCanteens(@Param("categoryIds") List<Integer> categoryIds, @Param("canteenIds") List<Integer> canteenIds, Pageable pageable);

    @Query("SELECT f FROM Food f JOIN f.category c WHERE c.categoryId IN :categoryIds AND LOWER(f.foodName) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Food> findByCategoriesAndName(@Param("categoryIds") List<Integer> categoryIds, @Param("name") String name, Pageable pageable);

    @Query("SELECT f FROM Food f JOIN f.canteen cn WHERE cn.canteenId IN :canteenIds AND LOWER(f.foodName) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Food> findByCanteensAndName(@Param("canteenIds") List<Integer> canteenIds, @Param("name") String name, Pageable pageable);

    @Query("SELECT f FROM Food f JOIN f.category c WHERE c.categoryId IN :categoryIds")
    Page<Food> findByCategoryIds(@Param("categoryIds") List<Integer> categoryIds, Pageable pageable);

    @Query("SELECT f FROM Food f JOIN f.canteen cn WHERE cn.canteenId IN :canteenIds")
    Page<Food> findByCanteenIds(@Param("canteenIds") List<Integer> canteenIds, Pageable pageable);

    @Query("SELECT f FROM Food f WHERE LOWER(f.foodName) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Food> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);


    @Query("SELECT f FROM Food f WHERE f.canteen.canteenId = :canteenId")
    List<Food> findByCanteenId(@Param("canteenId") Integer canteenId);

    @Query("SELECT f FROM Food f WHERE f.category.categoryId = ?1")
    List<Food> findByCategoryId(Integer categoryId);

}
