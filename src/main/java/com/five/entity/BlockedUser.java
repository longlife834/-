package com.five.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BlockedUser {
    private Long id;
    private Long blockerId;       // 拉黑者
    private Long blockedId;       // 被拉黑者
    private LocalDateTime createTime;
}
