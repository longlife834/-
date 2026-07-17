package com.five.dto;

import lombok.Data;

@Data
public class UserQueryDTO {
    private Integer page = 1;  //默认第一页
    private Integer size = 10; //每一页十条数据
    private String keyword;      // 搜索用户名或手机号
    private String role;         // USER / ADMIN
    private Boolean enabled;     // true / false
}