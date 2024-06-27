package com.ffood.g1.service;


import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrderService {


    List<Object[]> getBestSellingItems();

    List<Object[]> getOrderStats();
}
