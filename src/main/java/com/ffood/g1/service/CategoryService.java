package com.ffood.g1.service;

import com.ffood.g1.entity.Category;
import com.ffood.g1.entity.Food;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {

    List<Category> getAllCategories();



}

