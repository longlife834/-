package com.five.entity;

import lombok.Data;

@Data
public class Category {
    //分类的实体类
    private Long id;
    private String name;
    private Integer sortOrder;
}
