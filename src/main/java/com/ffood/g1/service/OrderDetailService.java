package com.ffood.g1.service;

import com.ffood.g1.entity.Order;
import com.ffood.g1.entity.OrderDetail;

import java.util.List;

public interface OrderDetailService {
    List<OrderDetail> getOrderDetailsByOrderId(Integer orderId);

}
