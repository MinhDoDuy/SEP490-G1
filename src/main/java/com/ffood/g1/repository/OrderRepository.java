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

    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderStatus = 'COMPLETE'")
    Long findTotalCompletedOrders();

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

    // Tìm kiếm order bị từ chối (REJECT)
    @Query("SELECT o FROM Order o " +
            "WHERE o.canteenId = :canteenId " +
            "AND o.orderStatus = 'REJECT' " +
            "AND LOWER(o.orderCode) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "ORDER BY o.orderDate DESC")
    Page<Order> searchRejectedOrdersByOrderCode(
            @Param("canteenId") Integer canteenId,
            @Param("keyword") String keyword,
            Pageable pageable);

    // Tìm kiếm order cần hoàn tiền (REFUND)
    @Query("SELECT o FROM Order o " +
            "WHERE o.canteenId = :canteenId " +
            "AND o.orderStatus = 'REFUND' " +
            "AND LOWER(o.orderCode) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "ORDER BY o.orderDate DESC")
    Page<Order> searchRefundedOrdersByOrderCode(@Param("canteenId") Integer canteenId,
                                                @Param("keyword") String keyword,
                                                Pageable pageable);



    //doanh thu theo tháng của canteen cụ thể
    @Query("SELECT TO_CHAR(o.orderDate, 'YYYY-MM'), SUM(CAST(od.price * od.quantity AS double)) " +
            "FROM Order o JOIN o.orderDetails od ON o.orderId = od.order.orderId " +
            "JOIN Food f ON od.food.foodId = f.foodId " +
            "WHERE o.orderStatus = 'COMPLETE' AND f.canteen.canteenId = :canteenId AND o.orderType ='ONLINE_ORDER'" +
            "GROUP BY TO_CHAR(o.orderDate, 'YYYY-MM') " +
            "ORDER BY TO_CHAR(o.orderDate, 'YYYY-MM')")
    List<Object[]> findRevenueCanteenDataByMonthOnline(@Param("canteenId") Integer canteenId);

    //doanh thu theo năm của canteen cụ thể
    @Query("SELECT TO_CHAR(o.orderDate, 'YYYY'), SUM(CAST(od.price * od.quantity AS double)) " +
            "FROM Order o JOIN o.orderDetails od ON o.orderId = od.order.orderId " +
            "JOIN Food f ON od.food.foodId = f.foodId " +
            "WHERE o.orderStatus = 'COMPLETE' AND f.canteen.canteenId = :canteenId AND o.orderType ='ONLINE_ORDER'" +
            "GROUP BY TO_CHAR(o.orderDate, 'YYYY') " +
            "ORDER BY TO_CHAR(o.orderDate, 'YYYY')")
    List<Object[]> findRevenueDataCanteenByYearOnline(@Param("canteenId") Integer canteenId);

    // Doanh thu theo tháng của canteen cụ thể tại quầy
    @Query("SELECT TO_CHAR(o.orderDate, 'YYYY-MM'), SUM(CAST(od.price * od.quantity AS double)) " +
            "FROM Order o JOIN o.orderDetails od ON o.orderId = od.order.orderId " +
            "JOIN Food f ON od.food.foodId = f.foodId " +
            "WHERE o.orderStatus = 'COMPLETE' AND f.canteen.canteenId = :canteenId AND o.orderType = 'AT_COUNTER' " +
            "GROUP BY TO_CHAR(o.orderDate, 'YYYY-MM') " +
            "ORDER BY TO_CHAR(o.orderDate, 'YYYY-MM')")
    List<Object[]> findRevenueCanteenDataByMonthAtCounter(@Param("canteenId") Integer canteenId);

    // Doanh thu theo năm của canteen cụ thể tại quầy
    @Query("SELECT TO_CHAR(o.orderDate, 'YYYY'), SUM(CAST(od.price * od.quantity AS double)) " +
            "FROM Order o JOIN o.orderDetails od ON o.orderId = od.order.orderId " +
            "JOIN Food f ON od.food.foodId = f.foodId " +
            "WHERE o.orderStatus = 'COMPLETE' AND f.canteen.canteenId = :canteenId AND o.orderType = 'AT_COUNTER' " +
            "GROUP BY TO_CHAR(o.orderDate, 'YYYY') " +
            "ORDER BY TO_CHAR(o.orderDate, 'YYYY')")
    List<Object[]> findRevenueDataCanteenByYearAtCounter(@Param("canteenId") Integer canteenId);


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

    //List đơn hàng trạng thái progress của nhân viên ship
    @Query("SELECT DISTINCT o FROM Order o " +
            "JOIN o.orderDetails od " +
            "JOIN od.food f " +
            "WHERE f.canteen.canteenId = :canteenId " +
            "AND o.deliveryRoleId = :deliveryRoleId " +
            "AND o.orderStatus = 'PROGRESS'"+
            "ORDER BY o.orderDate DESC ")
    Page<Order> findByCanteenIdAndDeliveryRoleIdAndStatusProgress(@Param("canteenId") Integer canteenId, @Param("deliveryRoleId") Integer deliveryRoleId, Pageable pageable);

    //List đơn hàng trạng thái complete của nhân viên ship
    @Query("SELECT DISTINCT o FROM Order o " +
            "JOIN o.orderDetails od " +
            "JOIN od.food f " +
            "WHERE f.canteen.canteenId = :canteenId " +
            "AND o.deliveryRoleId = :deliveryRoleId " +
            "AND o.orderStatus = 'COMPLETE' " +
            "ORDER BY o.orderDate DESC ")
    Page<Order> findByCanteenIdAndDeliveryRoleIdAndStatusComplete(@Param("canteenId") Integer canteenId, @Param("deliveryRoleId") Integer deliveryRoleId, Pageable pageable);



    @Query("SELECT o FROM Order o WHERE o.user.userId = :userId ORDER BY o.orderDate DESC")
    List<Order> findByUserId(@Param("userId") Integer userId);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.deliveryRoleId = :deliveryRoleId AND o.orderStatus = 'PROGRESS'")
    long countActiveOrdersByDeliveryRoleId(@Param("deliveryRoleId") Integer deliveryRoleId);

    @Query("SELECT o FROM Order o WHERE o.orderType = :orderType AND DATE(o.orderDate) = CURRENT_DATE ORDER BY o.orderDate DESC" )
    List<Order> findByOrderTypeAndCurrentDate(OrderType orderType);

    @Query("SELECT o.deliveryRoleId, COUNT(o.deliveryRoleId), SUM(o.totalOrderPrice)" +
            "FROM Order o " +
            "WHERE o.orderDate BETWEEN :startOfDay AND :endOfDay " +
            "AND o.orderType = :orderType " +
            "GROUP BY o.deliveryRoleId")
    List<Object[]> calculateSalesDataForTodayByOrderType(
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay,
            @Param("orderType") OrderType orderType);

    @Query("SELECT SUM(o.totalOrderPrice) FROM Order o WHERE o.canteenId = :canteenId " +
            "AND o.orderStatus = 'COMPLETE' " +
            "AND o.orderDate BETWEEN :startOfMonth AND :currentDate")
    Double findTotalRevenueForCurrentMonth(@Param("canteenId") Integer canteenId,
                                           @Param("startOfMonth") LocalDateTime startOfMonth,
                                           @Param("currentDate") LocalDateTime currentDate);
}
