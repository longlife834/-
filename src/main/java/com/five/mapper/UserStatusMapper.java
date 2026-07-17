package com.five.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserStatusMapper {
    /** 查询用户是否启用 */
    int isEnabled(@Param("userId") Long userId);
}
