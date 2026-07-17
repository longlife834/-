package com.five.dto;

import lombok.Data;

@Data
public class AdminUserCreateDTO {
    private String username;
    private String password;
    private String email;
    private String phone;
    private String avatar;
    private String role;
    private Boolean enabled;
}
