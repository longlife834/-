package com.five.mapper;

import com.five.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
//查询用户 根据用户名查询用户
    User getUserByName(@Param("username") String username);
//新增用户
    int insertUser(User user);
//查询用户 根据用户ID查询用户
    User getUserById(@Param("id") Long id);
// 分页查询用户列表
    List<User> getAllUsers(@Param("offset") int offset,
                           @Param("size") int size,
                           @Param("keyword") String keyword,
                           @Param("role") String role,
                           @Param("enabled") Boolean enabled);
// 统计用户总数
    long countUser(@Param("keyword") String keyword,
                   @Param("role") String role,
                   @Param("enabled") Boolean enabled);
//更新用户信息（管理员）
    int updateUser(User user);
//更新用户状态
    int updateUserStatus(@Param("id") Long id, @Param("enabled") Boolean enabled);
// 删除用户（逻辑删除）
    int deleteUserById(@Param("id") Long id);
// 修改密码
    int updatePassword(@Param("id") Long id, @Param("password") String password);
}
