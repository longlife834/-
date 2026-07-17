package com.five.mapper;

import com.five.entity.Conversation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ConversationMapper {
    /**
     * 用户点击“联系TA”
     * ConversationMapper 查找是否存在会话
     * 存在 → 复用已有会话
     * 不存在 → 创建新会话
     * 发送消息后，更新 lastMessage / lastTime
     * */
    List<Conversation> findByUserId(@Param("userId") Long userId);
    Conversation findById(@Param("id") Long id);
    Conversation findByUserPair(
        @Param("user1Id") Long user1Id,
        @Param("user2Id") Long user2Id,
        @Param("itemType") String itemType,
        @Param("itemId") Long itemId
    );
    //创建和更新会话
    int insert(Conversation conversation);
    int updateLastMessage(@Param("id") Long id,
                          @Param("lastMessage") String lastMessage,
                          @Param("lastTime") java.time.LocalDateTime lastTime);
    //未读统计
    int countUnreadByUserId(@Param("userId") Long userId);
   //删除会话（软删除）用户a删除了会话，用户b还是可以看见会话的
    int markDeletedForUser(@Param("id") Long id, @Param("userId") Long userId);
   //如果两个用户都删除了就是物理删除
    int deleteIfBothDeleted(@Param("id") Long id);
}
