package com.five.dto;

import lombok.Data;

@Data
public class AdminUserUpdateDTO {
    private String email;
    private String phone;
    private String avatar;
    private String role;         // USER / ADMIN
    private Boolean enabled;     // 启用/禁用
}
