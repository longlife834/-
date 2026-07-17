package com.five.mapper;

import com.five.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NotificationMapper {
    int insert(Notification notification);
    int markAsRead(@Param("id") Long id, @Param("userId") Long userId);
    int markAllAsRead(@Param("userId") Long userId);
    int countUnread(@Param("userId") Long userId);
    List<Notification> listByUser(@Param("userId") Long userId,
                                   @Param("offset") int offset,
                                   @Param("size") int size);
}
