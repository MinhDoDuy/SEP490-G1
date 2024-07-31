package com.ffood.g1.repository;

import com.ffood.g1.entity.Cart;
import com.ffood.g1.entity.CartItem;
import com.ffood.g1.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

//    Optional<CartItem> findByCartAndFood(Cart cart, Food food);
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart = :cart AND ci.food = :food AND ci.cart.status = 'active'")
    Optional<CartItem> findByCartAndFood(@Param("cart") Cart cart, @Param("food") Food food);


    @Query("SELECT ci FROM CartItem ci JOIN ci.cart c WHERE c.user.userId = :userId")
    List<CartItem> findCartItemsByUserId(@Param("userId") Integer userId);


    List<CartItem> findByCart(Cart cart);
    @Query("SELECT ci FROM CartItem ci WHERE ci.cartItemId = :cartItemId")
    CartItem getCartItemByCartItemId(Integer cartItemId);

    List<CartItem> findByCartItemIdIn(List<Integer> cartItemIds);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cartItemId IN :ids")
    void deleteByIds(@Param("ids") List<Integer> ids);
}