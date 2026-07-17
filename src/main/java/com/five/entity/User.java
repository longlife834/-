package com.five.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
public class User implements UserDetails {
    private Long id;
    private String username;
    @JsonIgnore   //接收用户信息信息时不会返回密码，前端传得密码会被忽略掉
    private String password;
    private String email;
    private String phone;
    private String avatar;
    private String role;
    private boolean enabled = true;
    private Boolean isDeleted = false;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
   //Spring Security 通过这个方法获取用户的角色/权限。
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role == null || role.isBlank()) {
            return List.of();
        }
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
