package com.five.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MessageDTO {
    private Long id; // 消息 ID（主键）
    private Long conversationId;// 所属会话 ID
    private Long senderId;   // 发送者用户 ID
    private String senderName; // 发送者用户名
    private String senderAvatar; // 发送者头像
    private String content;// 消息内容
    private Integer isRead;  // 是否已读：0-未读，1-已读
    private Boolean mine;  // true = 自己发的，false = 对方发的
    private LocalDateTime createTime;// 发送时间
}

