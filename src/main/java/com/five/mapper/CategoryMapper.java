package com.five.mapper;

import com.five.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CategoryMapper {
//获取所有分类
    List<Category> getAllCategories(@Param("offset") int offset,
                                    @Param("size") int size,
                                    @Param("keyword") String keyword);
//统计所有分类
    long countCategories(@Param("keyword") String keyword);
//获取分类ID
    Category getCategoryById(@Param("id") Long id);
//分类名称
    Category getCategoryByName(@Param("name") String name);
//新增分类
    int insertCategory(Category category);
//修改分类
    int updateCategory(Category category);
//删除分类
    int deleteCategory(@Param("id") Long id);
//获取所有分类的完整列表
    List<Category> getAllCategoriesSimple();
}
