package com.five.controller;

import com.five.entity.User;
import com.five.mapper.UserMapper;
import com.five.tools.JwtUtil;
import com.five.tools.LoginRequest;
import com.five.tools.RegisterRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping({"/auth", "/api/auth"})
public class LoginController {
    /** 用户打开登录页面 输入用户名和密码 ，调用Login方法 校验用户是否存在，校验密码是否正确
     * 生成JWT token 返回给前端 然后前端存储token  ，后续请求带上token
     *
     */

    //spring Security 的认证管理
    private final AuthenticationManager authenticationManager;
    //负责生成和解析token
    private final JwtUtil jwtUtil;
    //操作数据库查询用户
    private final UserMapper userMapper;
    //密码加密，用于密码匹配
    private final PasswordEncoder passwordEncoder;

    public LoginController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 登入验证
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        //1.查询用户是否存在
        User user = userMapper.getUserByName(request.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "用户不存在"));
        }
        //2.验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "密码错误"));
        }
        //3.检查账户是否被禁用
        if (!user.isEnabled()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "该账号已被禁用，请联系管理员"));
        }
        //4.生成令牌
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        //5.返回token
        return ResponseEntity.ok(token);
    }

    /*
    注册模块
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        /*
        检查用户名是否存在,如果存在就返回错误，否则创建新用户 ,默认普通用户,状态为启用，然后保存到数据库
         */
        User existuser = userMapper.getUserByName(request.getUsername());
        if (existuser != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "用户名已存在"));
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword())); //Bcrypt加密
        user.setEmail(request.getEmail());
        user.setRole("USER");
        user.setEnabled(true);
        userMapper.insertUser(user);
        return ResponseEntity.ok(Map.of("message", "注册成功"));
    }

}
