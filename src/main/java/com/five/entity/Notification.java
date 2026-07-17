package com.five.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Notification {
    private Long id;
    private Long userId;
    private String type;          // match / system
    private String title;
    private String content;
    private Long relatedId;       // 关联的物品ID
    private String relatedType;   // lost / found
    private Boolean isRead;
    private LocalDateTime createTime;
}
