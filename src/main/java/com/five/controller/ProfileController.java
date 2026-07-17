package com.five.controller;

import com.five.dto.ChangePasswordDTO;
import com.five.dto.UserProfileUpdateDTO;
import com.five.entity.User;
import com.five.service.UserService;
import com.five.tools.JwtUtil;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping({"/user/profile", "/api/user/profile"})
public class ProfileController {

    /*普通用户模块
    * */

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public ProfileController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public User getProfile(@AuthenticationPrincipal String username) {
        return userService.getUserByName(username);
    }

    @PutMapping
    public Map<String, Object> updateProfile(@AuthenticationPrincipal String username,
                                             @RequestBody UserProfileUpdateDTO dto) {
        User user = userService.updateUserProfile(username, dto);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", user.getId());
        result.put("username", user.getUsername());
        result.put("email", user.getEmail());
        result.put("phone", user.getPhone());
        result.put("avatar", user.getAvatar());
        result.put("role", user.getRole());
        // 如果用户名变了，签发新 token，否则前端拿旧 token 请求会被 JwtFilter 拦截
        if (!username.equals(user.getUsername())) {
            String newToken = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
            result.put("token", newToken);
        }
        return result;
    }
//AuthenticationPrincipal String username 当前登入的用户
    @PutMapping("/password")
    public Map<String, String> changePassword(@AuthenticationPrincipal String username,
                                               @RequestBody ChangePasswordDTO dto) {
        userService.changePassword(username, dto.getOldPassword(), dto.getNewPassword());
        return Map.of("message", "密码修改成功");
    }
}
