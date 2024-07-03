package com.ffood.g1.repository;

import com.ffood.g1.entity.Cart;
import com.ffood.g1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
//       Optional<Cart> findByUser(User user);
//    @Query("SELECT c FROM Cart c WHERE c.user = :user")
//    List<Cart> findByUser(@Param("user") User user);

    @Query("SELECT c FROM Cart c WHERE c.user = :user AND c.status = 'active'")
    Optional<Cart> findByUser(@Param("user") User user);

    @Query("SELECT c FROM Cart c WHERE c.user.userId = :userId")
    Optional<Cart> findByUserId(@Param("userId") Integer userId);

    @Query("SELECT SUM(ci.quantity) FROM Cart c JOIN c.cartItems ci WHERE c.user = :user AND c.status = 'active'")
    Integer getTotalQuantityByUser(@Param("user") User user);


//    @Query("SELECT SUM(ci.totalFoodPrice) FROM Cart c JOIN c.cartItems ci WHERE c.user = :user AND c.status = 'active'")
//    double getTotalFoodPriceByUser(@Param("user") User user);

    @Query("SELECT c.cartId FROM Cart c WHERE c.user.userId = :userId AND c.status = 'active'")
    Integer findCartIdByUserId(@Param("userId") Integer userId);

    @Query("SELECT SUM(ci.totalFoodPrice) FROM Cart c JOIN c.cartItems ci WHERE c.cartId = :cartId AND c.status = 'active'")
    Integer getTotalFoodPriceByCartId(@Param("cartId") Integer cartId);
}