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
       Optional<Cart> findByUser(User user);
//    @Query("SELECT c FROM Cart c WHERE c.user = :user")
//    List<Cart> findByUser(@Param("user") User user);

    @Query("SELECT c FROM Cart c WHERE c.user.userId = :userId")
    Optional<Cart> findByUserId(@Param("userId") Integer userId);
}