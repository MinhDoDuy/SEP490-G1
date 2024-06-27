package com.ffood.g1.repository;

import com.ffood.g1.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query("SELECT f.foodName, SUM(od.quantity) " +
            "FROM OrderDetail od JOIN od.food f " +
            "GROUP BY f.foodName " +
            "ORDER BY SUM(od.quantity) DESC")
    List<Object[]> findBestSellingItems();

    @Query("SELECT c.canteenName, COUNT(o.orderId) " +
            "FROM Order o JOIN o.orderDetails od JOIN od.food f JOIN f.canteen c " +
            "WHERE o.status = 'Complete' " +
            "GROUP BY c.canteenName " +
            "ORDER BY COUNT(o.orderId) DESC")
    List<Object[]> findOrderStats();
}
