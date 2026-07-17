package com.five.mapper;

import com.five.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageMapper {
    /***
     *       用户发送消息
     * MessageMapper.insert() → 写入数据库
     * 对方打开聊天窗口
     * MessageMapper.findByConversationId() → 加载历史消息
     * MessageMapper.markAsRead() → 标记消息已读
     */
//查询消息历史 	获取某个会话的所有消息历史
    List<Message> findByConversationId(@Param("conversationId") Long conversationId);
    //发送消息 保存一条新消息到数据库
    int insert(Message message);
    //标记消息已读 标记某个会话中，发给指定用户的所有消息为“已读”
    int markAsRead(@Param("conversationId") Long conversationId,
                   @Param("userId") Long userId);
    //统计未读消息数量
    int countUnread(@Param("conversationId") Long conversationId,
                    @Param("userId") Long userId);

   //获取最后一条消息 获取某个会话的最后一条消息
   Message findLastByConversationId(@Param("conversationId") Long conversationId);
   //清除会话的所有消息
    int deleteByConversationId(@Param("conversationId") Long conversationId);
}
