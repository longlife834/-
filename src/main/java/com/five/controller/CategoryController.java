package com.five.controller;

import com.five.entity.Category;
import com.five.service.CategoryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/admin", "/api/admin"})
@PreAuthorize("hasRole('ADMIN')") //权限控制，所有接口只有管理员才可以操作
public class CategoryController {
     // 用户登入后进入后台分类管理界面，调用categorycontroller接口，对category进行crud操作
     //前端下拉框调用、admin/categorys/all 获取所有分类，用户在发布/招领时可以选择分类
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/categories")
    public Map<String, Object> listCategories(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        List<Category> list = categoryService.getCategoryList(page, size, keyword);
        long total = categoryService.getCategoryCount(keyword);
        Map<String, Object> map = new HashMap<>();
        map.put("list", list);
        map.put("total", total);
        map.put("page", page);
        map.put("size", size);
        return map;
    }
    //查询所有分类，返回一个list列表
    @GetMapping("/categories/all")
    public List<Category> getAllCategories() {
        return categoryService.getAllCategoriesSimple();
    }

    /**
     *
     * @param id 根据ID查询分类
     * @return 返回当前分类的名称和排序号
     */
    @GetMapping("/categories/{id}")
    public Category getCategory(@PathVariable("id") Long id) {
        return categoryService.getCategoryById(id);
    }

    /**
     *
     * @param body 创建分类
     * @return
     */
    @PostMapping("/categories")
    public String createCategory(@RequestBody Map<String, Object> body) {
        String name = (String) body.get("name");
        Integer sortOrder = body.get("sortOrder") != null ?
                ((Number) body.get("sortOrder")).intValue() : 0;
        categoryService.createCategory(name, sortOrder);
        return "创建成功";
    }

    /**
     *
     * @param id 分类ID
     * @param body 更新分类
     * @return
     */
    @PutMapping("/categories/{id}")
    public String updateCategory(@PathVariable("id") Long id,
                                  @RequestBody Map<String, Object> body) {
        String name = (String) body.get("name");
        Integer sortOrder = body.get("sortOrder") != null ?
                ((Number) body.get("sortOrder")).intValue() : null;
        categoryService.updateCategory(id, name, sortOrder);
        return "更新成功";
    }

    @DeleteMapping("/categories/{id}")
    public String deleteCategory(@PathVariable("id") Long id) {
        categoryService.deleteCategory(id);
        return "删除成功";
    }
}
