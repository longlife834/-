package com.five.tools;

import com.five.entity.User;
import com.five.mapper.UserMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    public JwtFilter(JwtUtil jwtUtil, UserMapper userMapper) {
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        //从请求头获取token
        String header = request.getHeader("Authorization");
        String token = null;
        if (header != null && header.startsWith("Bearer")){
            token = header.substring(7);
        }
        //没有token就放行，等待后续security会拦截认证请求
        if (token == null || token.isEmpty()){
            filterChain.doFilter(request,response);
            return;
        }
        //开始校验token
        try{
            if(!jwtUtil.validateToken(token)){
                //token无效，清除上下文
                SecurityContextHolder.clearContext();
                filterChain.doFilter(request,response);
                return;
            }
            //解析token，获取用户信息和角色
            Claims claims = jwtUtil.parseToken(token);
            String username = claims.getSubject();
            Long userId = jwtUtil.getUserIdFromToken(token);
            // 检查用户是否仍处于启用状态
            User user = userMapper.getUserByName(username);
            if (user == null || !user.isEnabled()) {
                SecurityContextHolder.clearContext();
                filterChain.doFilter(request, response);
                return;
            }
            String role = claims.get("role").toString();
            //构建权限列表
            List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_"+role));
            //构建认证对象：principal=username, credentials=userId
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, userId, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            //放入安全上下文
            SecurityContextHolder.getContext().setAuthentication(authentication);

        }catch (Exception e){
            //Token无效清除上下文
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request,response);

    }
}
