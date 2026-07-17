package com.five.dto;

import lombok.Data;
import java.util.List;

@Data
public class HomeFeedDTO {
    //概览统计数据
    private OverviewStats overview;
    private List<FeedItem> activities;//动态活动列表
    private List<ResolvedItem> resolvedBanner; //已解决轮播，最近归还的的物品
    private List<DailyTrend> weeklyTrend;  //每周趋势的数据，采用折线图的形式

    /**
     * 概览统计
     */
    @Data
    public static class OverviewStats {
        private long pendingItems;       // 待认领物品总数
        private long weeklyNewItems;     // 本周新增
        private long resolvedItems;      // 已成功归还
        private long totalUsers;         // 注册用户
    }
    //动态活动列表
    @Data
    public static class FeedItem {
        private String itemType;         // "lost" or "found" 显示不同的图标和标签
        private Long itemId;     //点击跳转到详情页
        private String title;   //动态标题
        private Long userId;   //用户跳转到用户主页
        private String username;  //发布者姓名
        private String avatar;    //发布者头像
        private String location;   //发布者捡到或者丢失的地点
        private String categoryName;  //分类名称
        private String createTime;       // 发布时间
    }
   //已解决的轮播图
    @Data
    public static class ResolvedItem {
        private String itemType;   // "lost" or "found" 显示不同的图标和标签
        private Long itemId;   //物品id 点击跳转
        private String title;  //物品名称 显示标题
        private String username; //发布者姓名
        private String resolvedTime; //归还时间
    }
//每周趋势图
    @Data
    public static class DailyTrend {
        private String date;  //日期
        private String dayOfWeek; //星期几
        private long lostCount; //当天发布的总数
        private long foundCount; //当天招领的总数
    }
}
