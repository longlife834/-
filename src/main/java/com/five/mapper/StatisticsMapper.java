package com.five.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface StatisticsMapper {
//数据统计和首页聚合的 Mapper 接口。负责从多张表中聚合统计数据，
// 为管理后台数据看板和用户首页动态提供数据支撑。

//    管理员看板概览统计
    Map<String, Object> getOverviewStats();
//失物状态分布（饼图）
    List<Map<String, Object>> getLostStatusDistribution();
//招领状态分布（饼图）
    List<Map<String, Object>> getFoundStatusDistribution();
//分类分布（柱状图）
    List<Map<String, Object>> getCategoryDistribution();
//近7天趋势（折线图）
    List<Map<String, Object>> getWeeklyTrend();
//用户首页概览统计
    Map<String, Object> getHomeOverviewStats();
//	用户首页动态列表
    List<Map<String, Object>> getRecentActivities();
//用户首页已解决轮播
    List<Map<String, Object>> getRecentlyResolved();
}
