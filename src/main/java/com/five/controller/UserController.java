package com.five.controller;

import com.five.annotation.OperationLog;
import com.five.dto.AdminUserCreateDTO;
import com.five.dto.AdminUserUpdateDTO;
import com.five.dto.UserQueryDTO;
import com.five.entity.User;
import com.five.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/admin", "/api/admin"})
@PreAuthorize("hasRole('ADMIN')")
public class UserController {
  //用户管理页面
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public Map<String,Object> listUsers(UserQueryDTO userQueryDTO){
        List<User> list = userService.getUserList(userQueryDTO);
        long total = userService.getUserCount(userQueryDTO);
        Map<String,Object> map = new HashMap<>();
        map.put("list", list);
        map.put("total", total);
        map.put("page", userQueryDTO.getPage());
        map.put("size", userQueryDTO.getSize());
        return map;
    }

    /**
     *
     * @param createDTO 创建用户，管理员新增
     * @return  @operation日志会记录谁在什么时候创建了哪个用户
     */
    @OperationLog(module = "用户管理", operation = "新增", description = "创建用户")
    @PostMapping("/users")
    public String createUser(@RequestBody AdminUserCreateDTO createDTO){
        userService.createUser(createDTO);
        return "创建成功";
    }

    /**
     *
     * @param id 工具id查询用户
     * @return
     */
    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable("id") Long id){
        return userService.getUserById(id);
    }

    /**
     *
     * @param id
     * @param userUpdateDTO  修改用户信息
     * @return @operation日志会记录谁在什么时候修改了哪个用户
     */
    @OperationLog(module = "用户管理", operation = "修改", description = "编辑用户信息")
    @PutMapping("/users/{id}")
    public String updateUser(@PathVariable("id") Long id, @RequestBody AdminUserUpdateDTO userUpdateDTO){
        userService.updateUser(id, userUpdateDTO);
        return "更新成功";
    }

    /**
     *
     * @param id
     * @param status 启动和禁用用户的状态 Service 层禁止管理员禁用自己
     * @return
     */
    @OperationLog(module = "用户管理", operation = "启用禁用", description = "切换用户状态")
    @PatchMapping("/users/{id}/status")
    public String updateUserStatus(@PathVariable("id") Long id, @RequestBody Map<String, Boolean> status){
        Boolean enabled = status.get("enabled");
        if (enabled == null) {
            throw new IllegalArgumentException("enabled 字段不能为空");
        }
        userService.updateUserStatus(id, enabled);
        return "用户状态更新成功";
    }

    /**
     *
     * @param id 删除用户
     * @return @operation日志会记录谁在什么时候删除了哪个用户
     */
    @OperationLog(module = "用户管理", operation = "删除", description = "删除用户")
    @DeleteMapping("/users/{id}")
    public String deleteUser(@PathVariable("id") Long id){
        userService.deleteUserById(id);
        return "删除成功";
    }
}
