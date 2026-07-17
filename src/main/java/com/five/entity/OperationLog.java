package com.five.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OperationLog {
    //操作日志实体类
    private Long id;              // 主键 ID，自增
    private String username;      // 操作人用户名
    private String module;        // 操作模块（如 "用户管理"、"失物管理"）
    private String operation;     // 操作类型（如 "删除"、"新增"、"修改"）
    private String description;   // 操作描述（具体内容）
    private String ip;            // 操作时 IP 地址
    private LocalDateTime createTime; // 操作时间
}
