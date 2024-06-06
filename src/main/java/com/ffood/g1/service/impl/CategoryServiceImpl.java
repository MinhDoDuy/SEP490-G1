package com.ffood.g1.service.impl;

import com.ffood.g1.entity.Category;
import com.ffood.g1.repository.CategoryRepository;
import com.ffood.g1.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}
