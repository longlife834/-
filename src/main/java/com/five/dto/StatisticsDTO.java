package com.five.dto;

import lombok.Data;

import java.util.List;

@Data
public class StatisticsDTO {

    private OverviewStats overview;
    private List<StatusDistribution> lostStatusDistribution;
    private List<StatusDistribution> foundStatusDistribution;
    private List<CategoryDistribution> categoryDistribution;
    private List<DailyTrend> weeklyTrend;
    //概览统计
    @Data
    public static class OverviewStats {
        private long totalLostItems;  //失物总数
        private long totalFoundItems; // 招领总数
        private long pendingLostItems; //待认领失物总数
        private long pendingFoundItems; //代认领招领数
        private long resolvedItems;//已归还总数
        private long totalUsers;//注册用户总数
    }
   //状态分布
    @Data
    public static class StatusDistribution {
        private int status;  //状态码 0待认领 1认领中 2已归还
        private String statusName; //状态的中文名
        private long count; //该状态的总数
    }
  //分类分布
    @Data
    public static class CategoryDistribution {
        private Long categoryId; //分类ID
        private String categoryName; //分类名称
        private long lostCount; //该分类下失物数量
        private long foundCount;//该分类下招领数量
        private long total; //该分类下总数
    }
  //每日趋势
    @Data
    public static class DailyTrend {
        private String date; //星期
        private String dayOfWeek; //星期几
        private long lostCount;//当天失物发布数
        private long foundCount;//当天招领发布数
    }
}
