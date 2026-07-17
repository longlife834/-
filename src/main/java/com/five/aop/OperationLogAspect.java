package com.five.aop;

import com.five.annotation.OperationLog;
import com.five.mapper.OperationLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

@Aspect  //告诉Spring这是一个切面类
@Component
public class OperationLogAspect {
    //自动记录用户的操作日志，并保存到数据库
    private static final Logger log = LoggerFactory.getLogger(OperationLogAspect.class);
    //负责将日志插入到数据库
    private final OperationLogMapper operationLogMapper;

    public OperationLogAspect(OperationLogMapper operationLogMapper) {
        this.operationLogMapper = operationLogMapper;
    }

    @Around("@annotation(com.five.annotation.OperationLog)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 执行目标方法
        Object result = joinPoint.proceed();

        try {
            //获取注解信息和模块的名称
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            OperationLog opLog = method.getAnnotation(OperationLog.class);

            com.five.entity.OperationLog entity = new com.five.entity.OperationLog();

            // 获取当前操作用户
            String username = "anonymous";
            try {
                //从Security的上下文种1获取当前登入用户的用户名
                username = SecurityContextHolder.getContext().getAuthentication().getName();
            } catch (Exception ignored) {}
            //如果获取失败，则记录为anonymous
            entity.setUsername(username);

            // 模块和操作类型
            entity.setModule(opLog.module());
            entity.setOperation(opLog.operation());

            // 描述（简单拼接方法参数）
            String desc = opLog.description();
            if (desc.isEmpty()) {
                Object[] args = joinPoint.getArgs();
                StringBuilder sb = new StringBuilder();
                String[] paramNames = signature.getParameterNames();
                for (int i = 0; i < args.length; i++) {
                    if (args[i] != null && !(args[i] instanceof HttpServletRequest)) {
                        if (sb.length() > 0) sb.append(", ");
                        sb.append(paramNames != null && i < paramNames.length ? paramNames[i] : "arg" + i)
                          .append("=").append(args[i]);
                    }
                }
                desc = sb.toString();
            }
            entity.setDescription(desc);

            try {

                // 获取请求IP
                ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attrs != null) {
                    HttpServletRequest request = attrs.getRequest();
                    //优先取X-F------For 这个是适用于经过代理/负载均衡的场景
                    String ip = request.getHeader("X-Forwarded-For");
                    //没有则取RemoteAddr ，链接真实的IP
                    if (ip == null || ip.isBlank()) ip = request.getRemoteAddr();
                    entity.setIp(ip);
                }
            } catch (Exception ignored) {}
            //保存日志到数据库 operation——log
            operationLogMapper.insert(entity);
        } catch (Exception e) {
            // 日志记录失败不影响业务
            log.warn("操作日志记录失败", e);
        }

        return result;
    }
}
