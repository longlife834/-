package com.five.dto;

import lombok.Data;

@Data
public class UserProfileUpdateDTO {
    //普通用户修改信息
    private String username;
    private String email;
    private String phone;
    private String avatar;
}
