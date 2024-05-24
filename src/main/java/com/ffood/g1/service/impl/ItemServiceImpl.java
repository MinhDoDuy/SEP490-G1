package com.ffood.g1.service.impl;


import com.ffood.g1.entity.Item;
import com.ffood.g1.repository.ItemRepository;
import com.ffood.g1.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private ItemRepository itemRepository;

    @Override
    public List<Item> getRandomItems() {
        Pageable limit = PageRequest.of(0, 12);
        return itemRepository.findRandomItems(limit);
    }

    @Override
    public Page<Item> getAllItems(Pageable pageable) {
        return itemRepository.findAll(pageable);
    }
}