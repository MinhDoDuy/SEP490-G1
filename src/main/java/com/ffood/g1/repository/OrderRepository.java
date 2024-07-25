package com.ffood.g1.repository;

import com.ffood.g1.entity.Order;
import com.ffood.g1.entity.User;
import com.ffood.g1.enum_pay.OrderStatus;
import com.ffood.g1.enum_pay.OrderType;
import com.ffood.g1.enum_pay.PaymentStatus;
import io.swagger.models.auth.In;
import com.ffood.g1.enum_pay.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query("SELECT f.foodName, SUM(od.quantity) " +
            "FROM Order o JOIN o.orderDetails od JOIN od.food f " +
            "WHERE o.orderStatus = 'COMPLETE' " +
            "GROUP BY f.foodName " +
            "ORDER BY SUM(od.quantity) DESC")
    List<Object[]> findBestSellingItems();

    @Query("SELECT c.canteenName, COUNT(o.orderId) " +
            "FROM Order o JOIN o.orderDetails od JOIN od.food f JOIN f.canteen c " +
            "WHERE o.orderStatus = 'COMPLETE' " +
            "GROUP BY c.canteenName " +
            "ORDER BY COUNT(o.orderId) DESC")
    List<Object[]> findOrderStats();


    @Query("SELECT TO_CHAR(o.orderDate, 'YYYY-MM'), SUM(CAST(od.price * od.quantity AS double)) " +
            "FROM Order o JOIN o.orderDetails od " +
            "WHERE o.orderStatus = 'COMPLETE' " +
            "GROUP BY TO_CHAR(o.orderDate, 'YYYY-MM') " +
            "ORDER BY TO_CHAR(o.orderDate, 'YYYY-MM')")
    List<Object[]> findRevenueDataByMonth();

    @Query("SELECT TO_CHAR(o.orderDate, 'YYYY'), SUM(CAST(od.price * od.quantity AS double)) " +
            "FROM Order o JOIN o.orderDetails od " +
            "WHERE o.orderStatus = 'COMPLETE' " +
            "GROUP BY TO_CHAR(o.orderDate, 'YYYY') " +
            "ORDER BY TO_CHAR(o.orderDate, 'YYYY')")
    List<Object[]> findRevenueDataByYear();


    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderStatus = 'COMPLETE'")
    Double findTotalOrder();
    List<Order> findByUserUserIdAndPaymentStatus(Integer userId, PaymentStatus paymentStatus);

    @Query("SELECT DISTINCT o FROM Order o " +
            "JOIN o.orderDetails od " +
            "WHERE o.canteenId = :canteenId " +
            "AND o.orderStatus IN :statuses " +
            "ORDER BY o.orderDate DESC")
    Page<Order> findOrdersByCanteenIdAndStatuses(@Param("canteenId") Integer canteenId, @Param("statuses") List<OrderStatus> statuses, Pageable pageable);

    @Query("SELECT DISTINCT o FROM Order o " +
            "JOIN o.user u " +
            "JOIN o.orderDetails od " +
            "JOIN od.food f " +
            "WHERE o.canteenId = :canteenId " +
            "AND o.orderStatus IN :statuses " +
            "AND o.orderType = :orderType " +
            "ORDER BY o.orderDate DESC")
    Page<Order> findOrdersByCanteenIdAndStatusesAndOrderTypePendingOnline(
            @Param("canteenId") Integer canteenId,
            @Param("statuses") List<OrderStatus> statuses,
            @Param("orderType") OrderType orderType,
            Pageable pageable
    );

    //doanh thu theo tháng của canteen cụ thể
    @Query("SELECT TO_CHAR(o.orderDate, 'YYYY-MM'), SUM(CAST(od.price * od.quantity AS double)) " +
            "FROM Order o JOIN o.orderDetails od ON o.orderId = od.order.orderId " +
            "JOIN Food f ON od.food.foodId = f.foodId " +
            "WHERE o.orderStatus = 'COMPLETE' AND f.canteen.canteenId = :canteenId " +
            "GROUP BY TO_CHAR(o.orderDate, 'YYYY-MM') " +
            "ORDER BY TO_CHAR(o.orderDate, 'YYYY-MM')")
    List<Object[]> findRevenueCanteenDataByMonth(@Param("canteenId") Integer canteenId);

    //doanh thu theo năm của canteen cụ thể
    @Query("SELECT TO_CHAR(o.orderDate, 'YYYY'), SUM(CAST(od.price * od.quantity AS double)) " +
            "FROM Order o JOIN o.orderDetails od ON o.orderId = od.order.orderId " +
            "JOIN Food f ON od.food.foodId = f.foodId " +
            "WHERE o.orderStatus = 'COMPLETE' AND f.canteen.canteenId = :canteenId " +
            "GROUP BY TO_CHAR(o.orderDate, 'YYYY') " +
            "ORDER BY TO_CHAR(o.orderDate, 'YYYY')")
    List<Object[]> findRevenueDataCanteenByYear(@Param("canteenId") Integer canteenId);

    //tính lượt order thành công của từng canteen
    @Query("SELECT COUNT(o) FROM Order o JOIN o.orderDetails od ON o.orderId = od.order.orderId " +
            "JOIN Food f ON od.food.foodId = f.foodId " +
            "WHERE o.orderStatus = 'COMPLETE' AND f.canteen.canteenId = :canteenId")
    Integer countCompletedOrdersByCanteenId(@Param("canteenId") Integer canteenId);


    //đơn thành công theo tháng của canteen
    @Query("SELECT TO_CHAR(o.orderDate, 'YYYY-MM'), COUNT(o.orderId) " +
            "FROM Order o JOIN o.orderDetails od JOIN od.food f JOIN f.canteen c " +
            "WHERE o.orderStatus = 'COMPLETE' AND c.canteenId = :canteenId " +
            "GROUP BY TO_CHAR(o.orderDate, 'YYYY-MM') " +
            "ORDER BY TO_CHAR(o.orderDate, 'YYYY-MM')")
    List<Object[]> findOrderStatsByCanteenAndMonth(@Param("canteenId") Integer canteenId);

    //đơn thành công theo năm của canteen
    @Query("SELECT TO_CHAR(o.orderDate, 'YYYY'), COUNT(o.orderId) " +
            "FROM Order o JOIN o.orderDetails od JOIN od.food f JOIN f.canteen c " +
            "WHERE o.orderStatus = 'COMPLETE' AND c.canteenId = :canteenId " +
            "GROUP BY TO_CHAR(o.orderDate, 'YYYY') " +
            "ORDER BY TO_CHAR(o.orderDate, 'YYYY')")
    List<Object[]> findOrderStatsByCanteenAndYear(@Param("canteenId") Integer canteenId);

    //các đồ ăn bán chạy của canteen đó
    @Query("SELECT f.foodName, SUM(od.quantity) " +
            "FROM Order o JOIN o.orderDetails od JOIN od.food f " +
            "WHERE o.orderStatus = 'COMPLETE' AND f.canteen.canteenId = :canteenId " +
            "GROUP BY f.foodName " +
            "ORDER BY SUM(od.quantity) DESC")
    List<Object[]> findBestSellingItemsByCanteen(@Param("canteenId") Integer canteenId);


    //filter order có trạng thái complete trong khoảng ngày
    @Query("SELECT DISTINCT o FROM Order o " +
            "JOIN o.orderDetails od " +
            "JOIN od.food f " +
            "WHERE f.canteen.canteenId = :canteenId " +
            "AND o.orderStatus = 'COMPLETE' " +
            "AND o.orderDate BETWEEN :startDate AND :endDate " +
            "ORDER BY o.orderDate DESC")
    Page<Order> findCompletedOrdersByCanteenAndDateRange(
            @Param("canteenId") Integer canteenId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );


    @Query("SELECT DISTINCT o FROM Order o " +
            "JOIN o.orderDetails od " +
            "JOIN od.food f " +
            "WHERE f.canteen.canteenId = :canteenId " +
            "AND o.deliveryRoleId = :deliveryRoleId " +
            "AND o.orderStatus = 'PROGRESS'")
    Page<Order> findByCanteenIdAndDeliveryRoleIdAndStatusProgress(@Param("canteenId") Integer canteenId, @Param("deliveryRoleId") Integer deliveryRoleId, Pageable pageable);


    @Query("SELECT o FROM Order o WHERE o.user.userId = :userId")
    List<Order> findByUserId(@Param("userId") Integer userId);
}
