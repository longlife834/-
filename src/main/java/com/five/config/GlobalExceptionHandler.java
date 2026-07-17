package com.five.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    //全局异常处理器，统一拦截后端抛出的异常，转换成格式一致的错误2响应返回给前端
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrity(DataIntegrityViolationException e) {
        log.error("数据完整性异常", e);
        String msg = "操作失败：数据冲突或存在关联数据";
        if (e.getCause() != null && e.getCause().getMessage() != null) {
            String cause = e.getCause().getMessage();
            if (cause.contains("foreign key") || cause.contains("a foreign key constraint")) {
                msg = "删除失败：该用户存在关联数据（失物/拾物帖子），请先清理相关数据";
            }
        }
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", msg));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntime(RuntimeException e) {
        log.error("运行时异常", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage() != null ? e.getMessage() : "请求处理失败"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException e) {
        log.error("参数异常", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage() != null ? e.getMessage() : "参数不合法"));
    }
}
