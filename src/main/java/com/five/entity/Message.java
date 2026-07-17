package com.five.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Message {
    private Long id;
    private Long conversationId;// 所属会话 ID
    private Long senderId;// 发送者用户 ID
    private String content;// 消息内容
    private Integer isRead;// 是否已读：0-未读，1-已读
    private LocalDateTime createTime;// 发送时间

    // Joined fields
    private String senderName;// 发送者用户名
    private String senderAvatar;// 发送者头像
}
