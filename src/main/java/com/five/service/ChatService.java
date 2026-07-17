package com.five.service;

import com.five.dto.*;
import com.five.entity.Conversation;
import com.five.entity.Message;
import com.five.entity.Notification;
import com.five.mapper.BlockedUserMapper;
import com.five.mapper.ConversationMapper;
import com.five.mapper.MessageMapper;
import com.five.mapper.NotificationMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ConversationMapper conversationMapper;
    private final MessageMapper messageMapper;
    private final BlockedUserMapper blockedUserMapper;
    private final NotificationMapper notificationMapper;

    public ChatService(ConversationMapper conversationMapper,
                       MessageMapper messageMapper,
                       BlockedUserMapper blockedUserMapper,
                       NotificationMapper notificationMapper) {
        this.conversationMapper = conversationMapper;
        this.messageMapper = messageMapper;
        this.blockedUserMapper = blockedUserMapper;
        this.notificationMapper = notificationMapper;
    }

    /** 前段调用get /chat/conversion 然后后端返回List<ConversationDTO> */
    public List<ConversationDTO> getConversations(Long userId) {
        List<Conversation> conversations = conversationMapper.findByUserId(userId);
        return conversations.stream().map(c -> {
            ConversationDTO dto = new ConversationDTO();
            dto.setId(c.getId());
            dto.setOtherUserId(c.getUser1Id().equals(userId) ? c.getUser2Id() : c.getUser1Id());
            dto.setOtherUsername(c.getOtherUsername());
            dto.setOtherAvatar(c.getOtherAvatar());
            dto.setItemType(c.getItemType());
            dto.setItemId(c.getItemId());
            dto.setLastMessage(c.getLastMessage());
            dto.setLastTime(c.getLastTime());
            dto.setUnreadCount(c.getUnreadCount());
            return dto;
        }).collect(Collectors.toList());
    }

    /** Get all messages in a conversation, mark unread ones as read */
    public List<MessageDTO> getMessages(Long conversationId, Long userId) {
        Conversation conv = conversationMapper.findById(conversationId);
        if (conv == null) {
            throw new RuntimeException("会话不存在");
        }
        verifyParticipant(conv, userId);

        messageMapper.markAsRead(conversationId, userId);
        List<Message> messages = messageMapper.findByConversationId(conversationId);
        return messages.stream().map(m -> {
            MessageDTO dto = new MessageDTO();
            dto.setId(m.getId());
            dto.setConversationId(m.getConversationId());
            dto.setSenderId(m.getSenderId());
            dto.setSenderName(m.getSenderName());
            dto.setSenderAvatar(m.getSenderAvatar());
            dto.setContent(m.getContent());
            dto.setIsRead(m.getIsRead());
            dto.setMine(m.getSenderId().equals(userId));
            dto.setCreateTime(m.getCreateTime());
            return dto;
        }).collect(Collectors.toList());
    }

    /** Send a message */
    @Transactional
    public MessageDTO sendMessage(Long conversationId, Long senderId, String content) {
        Conversation conv = conversationMapper.findById(conversationId);
        if (conv == null) {
            throw new RuntimeException("会话不存在");
        }
        verifyParticipant(conv, senderId);

        Long otherUserId = conv.getUser1Id().equals(senderId) ? conv.getUser2Id() : conv.getUser1Id();
        if (blockedUserMapper.existsBetween(senderId, otherUserId) > 0) {
            throw new RuntimeException("由于拉黑限制，无法发送消息");
        }

        Message message = new Message();
        message.setConversationId(conversationId);
        message.setSenderId(senderId);
        message.setContent(content);
        messageMapper.insert(message);

        conversationMapper.updateLastMessage(conversationId, content, LocalDateTime.now());

        // 给接收方写入通知
        try {
            Notification notif = new Notification();
            notif.setUserId(otherUserId);
            notif.setType("message");
            notif.setTitle("新消息");
            notif.setContent(content.length() > 50 ? content.substring(0, 50) + "..." : content);
            notif.setRelatedId(conversationId);
            notif.setRelatedType("conversation");
            notificationMapper.insert(notif);
        } catch (Exception ignored) {}

        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setConversationId(conversationId);
        dto.setSenderId(senderId);
        dto.setContent(content);
        dto.setIsRead(0);
        dto.setMine(true);
        dto.setCreateTime(message.getCreateTime());
        return dto;
    }

    /** Create or find a conversation */
    @Transactional
    public ConversationDTO createConversation(Long userId, CreateConversationRequest req) {
        Long otherUserId = req.getOtherUserId();
        if (userId.equals(otherUserId)) {
            throw new RuntimeException("不能和自己创建会话");
        }

        Long user1Id = userId < otherUserId ? userId : otherUserId;
        Long user2Id = userId < otherUserId ? otherUserId : userId;

        Conversation existing = conversationMapper.findByUserPair(
                user1Id, user2Id, req.getItemType(), req.getItemId());
        if (existing != null) {
            ConversationDTO dto = new ConversationDTO();
            dto.setId(existing.getId());
            dto.setOtherUserId(otherUserId);
            dto.setItemType(existing.getItemType());
            dto.setItemId(existing.getItemId());
            return dto;
        }

        Conversation conversation = new Conversation();
        conversation.setUser1Id(user1Id);
        conversation.setUser2Id(user2Id);
        conversation.setItemType(req.getItemType());
        conversation.setItemId(req.getItemId());
        conversationMapper.insert(conversation);

        ConversationDTO dto = new ConversationDTO();
        dto.setId(conversation.getId());
        dto.setOtherUserId(otherUserId);
        dto.setItemType(req.getItemType());
        dto.setItemId(req.getItemId());
        return dto;
    }

    /** Get total unread count */
    public Map<String, Integer> getUnreadCount(Long userId) {
        int msgUnread = conversationMapper.countUnreadByUserId(userId);
        int notifUnread = notificationMapper.countUnreadByUserId(userId);
        return Map.of("unread", msgUnread + notifUnread);
    }

    /** Delete (hide) conversation for current user */
    @Transactional
    public void deleteConversation(Long conversationId, Long userId) {
        Conversation conv = conversationMapper.findById(conversationId);
        if (conv == null) {
            throw new RuntimeException("会话不存在");
        }
        verifyParticipant(conv, userId);
        conversationMapper.markDeletedForUser(conversationId, userId);
        conversationMapper.deleteIfBothDeleted(conversationId);
    }

    /** Clear all messages in a conversation */
    @Transactional
    public void clearMessages(Long conversationId, Long userId) {
        Conversation conv = conversationMapper.findById(conversationId);
        if (conv == null) {
            throw new RuntimeException("会话不存在");
        }
        verifyParticipant(conv, userId);
        messageMapper.deleteByConversationId(conversationId);
    }

    /** Block the other user in a conversation */
    @Transactional
    public void blockUser(Long conversationId, Long userId) {
        Conversation conv = conversationMapper.findById(conversationId);
        if (conv == null) {
            throw new RuntimeException("会话不存在");
        }
        verifyParticipant(conv, userId);
        Long otherUserId = conv.getUser1Id().equals(userId) ? conv.getUser2Id() : conv.getUser1Id();
        if (blockedUserMapper.exists(userId, otherUserId) == 0) {
            blockedUserMapper.insert(userId, otherUserId);
        }
    }

    /** Unblock the other user in a conversation */
    @Transactional
    public void unblockUser(Long conversationId, Long userId) {
        Conversation conv = conversationMapper.findById(conversationId);
        if (conv == null) {
            throw new RuntimeException("会话不存在");
        }
        verifyParticipant(conv, userId);
        Long otherUserId = conv.getUser1Id().equals(userId) ? conv.getUser2Id() : conv.getUser1Id();
        blockedUserMapper.delete(userId, otherUserId);
    }

    /** Check block status between the two users */
    public Map<String, Boolean> isBlocked(Long conversationId, Long userId) {
        Conversation conv = conversationMapper.findById(conversationId);
        if (conv == null) return Map.of("iBlocked", false, "blockedBy", false);
        Long otherUserId = conv.getUser1Id().equals(userId) ? conv.getUser2Id() : conv.getUser1Id();
        boolean iBlocked = blockedUserMapper.exists(userId, otherUserId) > 0;
        boolean blockedBy = blockedUserMapper.exists(otherUserId, userId) > 0;
        return Map.of("iBlocked", iBlocked, "blockedBy", blockedBy);
    }

    private void verifyParticipant(Conversation conv, Long userId) {
        if (!userId.equals(conv.getUser1Id()) && !userId.equals(conv.getUser2Id())) {
            throw new RuntimeException("无权访问该会话");
        }
    }
}
