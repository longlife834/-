package com.five.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BlockedUserMapper {
    /**
     用户 A 拉黑用户 B
     调用 BlockedUserMapper.insert(A, B)
     插入一条记录到 blocked_user 表
     A 和 B 之间的消息被屏蔽
     取消拉黑时调用 delete(A, B)
     */
    int insert(@Param("blockerId") Long blockerId, @Param("blockedId") Long blockedId);
    int delete(@Param("blockerId") Long blockerId, @Param("blockedId") Long blockedId);
    /** 检查是否拉黑了某人 返回1表示已经拉黑，0表示未拉黑*/
    int exists(@Param("blockerId") Long blockerId, @Param("blockedId") Long blockedId);
    /** 检查双向拉黑关系 返回1表示存在拉黑关系，返回0则没有*/
    int existsBetween(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
}
