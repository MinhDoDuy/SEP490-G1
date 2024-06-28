package com.ffood.g1.service.impl;

import com.ffood.g1.repository.OrderRepository;
import com.ffood.g1.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;

    public List<Object[]> getBestSellingItems() {
        return orderRepository.findBestSellingItems();
    }

    public List<Object[]> getOrderStats() {
        return orderRepository.findOrderStats();
    }

    @Override
    public List<Object[]> getRevenueDataByDay() {
        return orderRepository.findRevenueDataByDay();
    }

    @Override
    public List<Object[]> getRevenueDataByMonth() {
        return orderRepository.findRevenueDataByMonth();
    }

    @Override
    public List<Object[]> getRevenueDataByYear() {
        return orderRepository.findRevenueDataByYear();
    }
}
