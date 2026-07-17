package com.five.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ConversationDTO {
    private Long id;
    private Long otherUserId;// 对方用户 ID
    private String otherUsername;// 对方用户名
    private String otherAvatar; // 对方头像
    private String itemType;   // 物品类型：lost 或 "found"
    private Long itemId;  // 物品 ID
    private String lastMessage;// 最后一条消息内容
    private LocalDateTime lastTime;// 最后一条消息的时间
    private Integer unreadCount;// 当前会话的未读消息数量
}
