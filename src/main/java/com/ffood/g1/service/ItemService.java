package com.ffood.g1.service;

import com.ffood.g1.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemService {
   //get items random in home
    List<Item> getRandomItems();

    //get all Items

    Page<Item> getAllItems(Pageable pageable);
}
