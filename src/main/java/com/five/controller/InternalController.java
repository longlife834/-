package com.five.controller;

import com.five.entity.User;
import com.five.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 内部接口 — 供 lost-service 通过 Feign 调用，不需要认证
 */
@RestController
@RequestMapping("/internal")
public class InternalController {
    /**
     * 内部接口控制器，韦其它微服务通过feign调用而设计，不对外暴露端口
     */
    private final UserService userService;

    public InternalController(UserService userService) {
        this.userService = userService;
    }

    /**
     *
     * @param id 根据id查询用户
     * @return
     */
    @GetMapping("/user/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    /**
     *
     * @param username  根据用户名查询用户
     * @return
     */
    @GetMapping("/username/{username}")
    public User getUserByUsername(@PathVariable String username) {
        return userService.getUserByName(username);
    }

    /**
     *
     * @param ids 批量查询用户
     * @return
     */
    @PostMapping("/users/batc")
    public List<User> getUsersByIds(@RequestBody List<Long> ids) {
        return ids.stream()
                .map(userService::getUserById)
                .filter(user -> user != null)
                .toList();
    }
}
