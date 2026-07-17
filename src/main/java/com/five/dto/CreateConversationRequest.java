package com.five.dto;

import lombok.Data;

@Data
public class CreateConversationRequest {
    //创建会话请求 DTO，用于前端发起聊天时告诉后端“我要和谁聊”、“聊的是哪个物品”。
    private Long otherUserId; //聊天对象的用户 ID
    private String itemType;//关联的物品类型（lost 或 "found"
    private Long itemId;//关联的物品 ID
}
