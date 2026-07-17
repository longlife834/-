package com.five.service;

import com.five.dto.AdminUserCreateDTO;
import com.five.dto.AdminUserUpdateDTO;
import com.five.dto.UserProfileUpdateDTO;
import com.five.dto.UserQueryDTO;
import com.five.entity.User;
import com.five.mapper.UserMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserService implements UserDetailsService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 获取当前登录用户的用户名
     */
    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = userMapper.getUserByName(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在：" + username);
        }
        return user;
    }
//获取当前用户列表
    public List<User> getUserList(UserQueryDTO userQueryDTO) {
        int offset = (userQueryDTO.getPage() - 1) * userQueryDTO.getSize();
        return userMapper.getAllUsers(
                offset,
                userQueryDTO.getSize(),
                userQueryDTO.getKeyword(),
                userQueryDTO.getRole(),
                userQueryDTO.getEnabled()
        );
    }
//获取用户总数
    public long getUserCount(UserQueryDTO queryDTO) {
        return userMapper.countUser(
                queryDTO.getKeyword(),
                queryDTO.getRole(),
                queryDTO.getEnabled()
        );
    }
//查询用户 通过用户ID查询用户
    public User getUserById(Long id) {
        return userMapper.getUserById(id);
    }
//查询用户 通过用户名查询用户
    public User getUserByName(String username) {
        return userMapper.getUserByName(username);
    }
//新增用户
    public void createUser(AdminUserCreateDTO createDTO) {
        if (createDTO.getUsername() == null || createDTO.getUsername().isBlank()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (userMapper.getUserByName(createDTO.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(createDTO.getUsername());
        user.setPassword(passwordEncoder.encode(createDTO.getPassword() == null ? "123456" : createDTO.getPassword()));
        user.setEmail(createDTO.getEmail());
        user.setPhone(createDTO.getPhone());
        user.setAvatar(createDTO.getAvatar());
        user.setRole(createDTO.getRole() == null ? "USER" : createDTO.getRole());
        user.setEnabled(createDTO.getEnabled() == null ? true : createDTO.getEnabled());
        userMapper.insertUser(user);
    }
//修改用户信息
    public void updateUser(Long id, AdminUserUpdateDTO updateDTO) {
        User user = userMapper.getUserById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        String currentUsername = getCurrentUsername();
        boolean isSelf = user.getUsername().equals(currentUsername);

        if (updateDTO.getEmail() != null) {
            user.setEmail(updateDTO.getEmail());
        }
        if (updateDTO.getPhone() != null) {
            user.setPhone(updateDTO.getPhone());
        }
        if (updateDTO.getAvatar() != null) {
            user.setAvatar(updateDTO.getAvatar());
        }
        if (updateDTO.getRole() != null && !updateDTO.getRole().isBlank()) {
            if (isSelf && !"ADMIN".equals(updateDTO.getRole())) {
                throw new RuntimeException("不能降级自己的角色");
            }
            user.setRole(updateDTO.getRole());
        }
        if (updateDTO.getEnabled() != null) {
            if (isSelf && !updateDTO.getEnabled()) {
                throw new RuntimeException("不能禁用自己的账号");
            }
            user.setEnabled(updateDTO.getEnabled());
        }
        userMapper.updateUser(user);
    }
//修改普通用户
    public User updateUserProfile(String username, UserProfileUpdateDTO updateDTO) {
        User user = userMapper.getUserByName(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (updateDTO.getEmail() != null) {
            user.setEmail(updateDTO.getEmail());
        }
        if (updateDTO.getPhone() != null) {
            user.setPhone(updateDTO.getPhone());
        }
        if (updateDTO.getAvatar() != null) {
            user.setAvatar(updateDTO.getAvatar());
        }
        if (updateDTO.getUsername() != null && !updateDTO.getUsername().isBlank()) {
            // 检查用户名是否已被占用
            User existing = userMapper.getUserByName(updateDTO.getUsername());
            if (existing != null && !existing.getId().equals(user.getId())) {
                throw new RuntimeException("该用户名已被占用");
            }
            user.setUsername(updateDTO.getUsername());
        }
        userMapper.updateUser(user);
        return user;
    }
//更新用户的状态
    public void updateUserStatus(Long id, Boolean enabled) {
        User user = userMapper.getUserById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!enabled && user.getUsername().equals(getCurrentUsername())) {
            throw new RuntimeException("不能禁用自己的账号");
        }
        userMapper.updateUserStatus(id, enabled);
    }
//删除用户
    public void deleteUserById(Long id) {
        User user = userMapper.getUserById(id);
        if (user != null && user.getUsername().equals(getCurrentUsername())) {
            throw new RuntimeException("不能删除自己的账号");
        }
        userMapper.deleteUserById(id);
    }
//修改密码
    public void changePassword(String username, String oldPassword, String newPassword) {
        User user = userMapper.getUserByName(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("旧密码错误");
        }
        if (oldPassword.equals(newPassword)) {
            throw new RuntimeException("新密码不能与旧密码相同");
        }
        userMapper.updatePassword(user.getId(), passwordEncoder.encode(newPassword));
    }

}
