package com.ffood.g1.repository;

import com.ffood.g1.entity.Order;
import com.ffood.g1.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    List<OrderDetail> findByOrderOrderId(Integer orderId);
    List<OrderDetail> findByOrder(Order order);
}
