package com.ffood.g1.repository;

import com.ffood.g1.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query("SELECT f.foodName, SUM(od.quantity) " +
            "FROM Order o JOIN o.orderDetails od JOIN od.food f " +
            "WHERE o.status = 'PAYMENT_COMPLETE' " +
            "GROUP BY f.foodName " +
            "ORDER BY SUM(od.quantity) DESC")
    List<Object[]> findBestSellingItems();

    @Query("SELECT c.canteenName, COUNT(o.orderId) " +
            "FROM Order o JOIN o.orderDetails od JOIN od.food f JOIN f.canteen c " +
            "WHERE o.status = 'PAYMENT_COMPLETE' " +
            "GROUP BY c.canteenName " +
            "ORDER BY COUNT(o.orderId) DESC")
    List<Object[]> findOrderStats();

    @Query("SELECT TO_CHAR(o.orderDate, 'YYYY-MM-DD'), SUM(CAST(od.price * od.quantity AS double)) " +
            "FROM Order o JOIN o.orderDetails od " +
            "WHERE o.status = 'PAYMENT_COMPLETE' " +
            "GROUP BY TO_CHAR(o.orderDate, 'YYYY-MM-DD') " +
            "ORDER BY TO_CHAR(o.orderDate, 'YYYY-MM-DD')")
    List<Object[]> findRevenueDataByDay();

    @Query("SELECT TO_CHAR(o.orderDate, 'YYYY-MM'), SUM(CAST(od.price * od.quantity AS double)) " +
            "FROM Order o JOIN o.orderDetails od " +
            "WHERE o.status = 'PAYMENT_COMPLETE' " +
            "GROUP BY TO_CHAR(o.orderDate, 'YYYY-MM') " +
            "ORDER BY TO_CHAR(o.orderDate, 'YYYY-MM')")
    List<Object[]> findRevenueDataByMonth();

    @Query("SELECT TO_CHAR(o.orderDate, 'YYYY'), SUM(CAST(od.price * od.quantity AS double)) " +
            "FROM Order o JOIN o.orderDetails od " +
            "WHERE o.status = 'PAYMENT_COMPLETE' " +
            "GROUP BY TO_CHAR(o.orderDate, 'YYYY') " +
            "ORDER BY TO_CHAR(o.orderDate, 'YYYY')")
    List<Object[]> findRevenueDataByYear();


    @Query("SELECT SUM(od.quantity * od.price) FROM Order o JOIN o.orderDetails od WHERE o.status = 'COMPLETE'")
    Double findTotalRevenue();
}
