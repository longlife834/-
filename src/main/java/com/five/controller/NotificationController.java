package com.five.controller;

import com.five.entity.Notification;
import com.five.mapper.NotificationMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping({"/notifications", "/api/notifications"})
public class NotificationController {

    private final NotificationMapper notificationMapper;

    public NotificationController(NotificationMapper notificationMapper) {
        this.notificationMapper = notificationMapper;
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getCredentials() instanceof Long) {
            return (Long) auth.getCredentials();
        }
        // fallback: try to look up by username
        return null;
    }

    /** 获取未读通知数量 */
    @GetMapping("/unread-count")
    public Map<String, Object> unreadCount() {
        Long userId = getCurrentUserId();
        int count = userId != null ? notificationMapper.countUnread(userId) : 0;
        return Map.of("count", count);
    }

    /** 获取通知列表（分页） */
    @GetMapping
    public Map<String, Object> list(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "20") int size) {
        Long userId = getCurrentUserId();
        if (userId == null) return Map.of("list", List.of(), "total", 0);

        int offset = (page - 1) * size;
        List<Notification> list = notificationMapper.listByUser(userId, offset, size);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("list", list);
        result.put("total", notificationMapper.countUnread(userId)); // total unread for simplicity
        result.put("page", page);
        return result;
    }

    /** 标记单条已读 */
    @PutMapping("/{id}/read")
    public Map<String, String> markRead(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        if (userId != null) notificationMapper.markAsRead(id, userId);
        return Map.of("message", "ok");
    }

    /** 全部标记已读 */
    @PutMapping("/read-all")
    public Map<String, String> markAllRead() {
        Long userId = getCurrentUserId();
        if (userId != null) notificationMapper.markAllAsRead(userId);
        return Map.of("message", "ok");
    }
}
