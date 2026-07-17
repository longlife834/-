package com.five.controller;

import com.five.annotation.OperationLog;
import com.five.dto.*;
import com.five.entity.Notification;
import com.five.mapper.NotificationMapper;
import com.five.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat")
public class ChatController {
    //聊天控制器
    private final ChatService chatService;
    private final NotificationMapper notificationMapper;

    public ChatController(ChatService chatService, NotificationMapper notificationMapper) {
        this.chatService = chatService;
        this.notificationMapper = notificationMapper;
    }
    //获取当前用户
    private Long getCurrentUserId() {
        //从 Spring Security 上下文中提取当前登录用户的 ID
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getCredentials() instanceof Long) {
            return (Long) auth.getCredentials();
        }
        throw new RuntimeException("请先登录");
    }
    //会话管理 获取当前用户得到会话列表，查看用户跟谁聊天
    @GetMapping("/conversations")
    public List<ConversationDTO> listConversations() {
        return chatService.getConversations(getCurrentUserId());
    }
    //消息管理   获取某个会话的历史信息
    @GetMapping("/conversations/{id}/messages")
    public List<MessageDTO> getMessages(@PathVariable Long id) {
        return chatService.getMessages(id, getCurrentUserId());
    }
    //创建会话
    @OperationLog(module = "聊天管理", operation = "新增", description = "发送消息")
    @PostMapping("/conversations/{id}/messages")
    public MessageDTO sendMessage(@PathVariable Long id, @RequestBody SendMessageRequest req) {
        return chatService.sendMessage(id, getCurrentUserId(), req.getContent());
    }
    @PostMapping("/conversations")
    public ConversationDTO createConversation(@RequestBody CreateConversationRequest req) {
        return chatService.createConversation(getCurrentUserId(), req);
    }
    //未读消息统计  获取当前用户所有会话的未读消息总数
    @GetMapping("/unread")
    public Map<String, Integer> getUnreadCount() {
        return chatService.getUnreadCount(getCurrentUserId());
    }

    @OperationLog(module = "聊天管理", operation = "删除", description = "删除会话")
    @DeleteMapping("/conversations/{id}")
    public ResponseEntity<?> deleteConversation(@PathVariable Long id) {
        chatService.deleteConversation(id, getCurrentUserId());
        return ResponseEntity.ok(Map.of("message", "删除成功"));
    }

    @OperationLog(module = "聊天管理", operation = "删除", description = "清除聊天记录")
    @DeleteMapping("/conversations/{id}/messages")
    public ResponseEntity<?> clearMessages(@PathVariable Long id) {
        chatService.clearMessages(id, getCurrentUserId());
        return ResponseEntity.ok(Map.of("message", "清除成功"));
    }
   //拉黑管理 拉黑会话中的对方用户，屏蔽其消息
    @OperationLog(module = "聊天管理", operation = "修改", description = "拉黑用户")
    @PostMapping("/conversations/{id}/block")
    public ResponseEntity<?> blockUser(@PathVariable Long id) {
        chatService.blockUser(id, getCurrentUserId());
        return ResponseEntity.ok(Map.of("message", "已拉黑"));
    }

    @OperationLog(module = "聊天管理", operation = "修改", description = "取消拉黑")
    @DeleteMapping("/conversations/{id}/block")
    public ResponseEntity<?> unblockUser(@PathVariable Long id) {
        chatService.unblockUser(id, getCurrentUserId());
        return ResponseEntity.ok(Map.of("message", "已取消拉黑"));
    }

    @GetMapping("/conversations/{id}/blocked")
    public Map<String, Boolean> isBlocked(@PathVariable Long id) {
        return chatService.isBlocked(id, getCurrentUserId());
    }
   //通知管理 获取当前用户的通知列表（分页）
    @GetMapping("/notifications")
    public Map<String, Object> listNotifications(@RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        Long userId = getCurrentUserId();
        int offset = (page - 1) * size;
        List<Notification> list = notificationMapper.listByUserId(userId, offset, size);
        int unread = notificationMapper.countUnreadByUserId(userId);
        return Map.of("list", list, "unread", unread, "page", page, "size", size);
    }

    @PostMapping("/notifications/read-all")
    public Map<String, String> markAllNotificationsRead() {
        notificationMapper.markAllAsRead(getCurrentUserId());
        return Map.of("message", "已全部标为已读");
    }

    @PostMapping("/notifications/{id}/read")
    public Map<String, String> markNotificationRead(@PathVariable Long id) {
        notificationMapper.markAsRead(id);
        return Map.of("message", "已读");
    }
}
