package com.five.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Conversation {
    private Long id;
    private Long user1Id; // 会话发起方用户 ID
    private Long user2Id;         // 会话接收方用户 ID
    private String itemType;// 关联的物品类型：lost / "found"
    private Long itemId;// 关联的物品 ID
    private String lastMessage; // 最后一条消息内容（冗余字段，减少查询）
    private LocalDateTime lastTime;// 最后一条消息时间
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updatedAt; // 更新时间

    // Joined fields (not in conversation table)
    private String otherUsername;// 对方用户名
    private String otherAvatar;  // 对方头像
    private Integer unreadCount; // 未读消息数量
}
