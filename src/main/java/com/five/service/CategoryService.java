package com.five.service;

import com.five.entity.Category;
import com.five.mapper.CategoryMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryService {

    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }
//获取分页列表
    public List<Category> getCategoryList(int page, int size, String keyword) {
        int offset = (page - 1) * size;
        return categoryMapper.getAllCategories(offset, size, keyword);
    }
//获取分类的数量统计
    public long getCategoryCount(String keyword) {
        return categoryMapper.countCategories(keyword);
    }
    //根据 ID 查询分类
    public Category getCategoryById(Long id) {
        Category category = categoryMapper.getCategoryById(id);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        return category;
    }
//创建分类
    public void createCategory(String name, Integer sortOrder) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("分类名称不能为空");
        }
        if (categoryMapper.getCategoryByName(name) != null) {
            throw new RuntimeException("分类名称已存在");
        }
        Category category = new Category();
        category.setName(name.trim());
        category.setSortOrder(sortOrder != null ? sortOrder : 0);
        categoryMapper.insertCategory(category);
    }
//修改分类
    public void updateCategory(Long id, String name, Integer sortOrder) {
        Category category = categoryMapper.getCategoryById(id);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        if (name != null && !name.isBlank()) {
            Category existing = categoryMapper.getCategoryByName(name.trim());
            if (existing != null && !existing.getId().equals(id)) {
                throw new RuntimeException("分类名称已存在");
            }
            category.setName(name.trim());
        }
        if (sortOrder != null) {
            category.setSortOrder(sortOrder);
        }
        categoryMapper.updateCategory(category);
    }
//删除分类
    public void deleteCategory(Long id) {
        Category category = categoryMapper.getCategoryById(id);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        categoryMapper.deleteCategory(id);
    }
//获取所有分类
    public List<Category> getAllCategoriesSimple() {
        return categoryMapper.getAllCategoriesSimple();
    }
}
