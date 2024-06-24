package com.ffood.g1.repository;

import com.ffood.g1.entity.Cart;
import com.ffood.g1.entity.CartItem;
import com.ffood.g1.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    Optional<CartItem> findByCartAndFood(Cart cart, Food food);

    @Query("SELECT ci FROM CartItem ci JOIN ci.cart c WHERE c.user.userId = :userId")
    List<CartItem> findCartItemsByUserId(@Param("userId") Integer userId);


}