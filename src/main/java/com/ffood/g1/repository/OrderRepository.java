package com.ffood.g1.repository;

import com.ffood.g1.entity.Order;
import com.ffood.g1.enum_pay.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findByUserUserIdAndStatus(Integer userId, OrderStatus status);
}
