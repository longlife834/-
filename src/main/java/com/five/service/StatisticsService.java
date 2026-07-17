package com.five.service;

import com.five.dto.HomeFeedDTO;
import com.five.dto.StatisticsDTO;
import com.five.mapper.StatisticsMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class StatisticsService {
    /**
     * StatisticsMapper（原始数据）
     * StatisticsService（组装 + 转换）
     * Controller（返回给前端）
     * 前端渲染 ECharts 图表 / 时间线
     */
    private final StatisticsMapper statisticsMapper;

    public StatisticsService(StatisticsMapper statisticsMapper) {
        this.statisticsMapper = statisticsMapper;
    }

    public StatisticsDTO getStatistics() {
        StatisticsDTO dto = new StatisticsDTO();

        // 1. 概览统计
        Map<String, Object> overviewMap = statisticsMapper.getOverviewStats();
        StatisticsDTO.OverviewStats overview = new StatisticsDTO.OverviewStats();
        overview.setTotalLostItems(toLong(overviewMap.get("total_lost_items")));
        overview.setTotalFoundItems(toLong(overviewMap.get("total_found_items")));
        overview.setPendingLostItems(toLong(overviewMap.get("pending_lost_items")));
        overview.setPendingFoundItems(toLong(overviewMap.get("pending_found_items")));
        overview.setResolvedItems(toLong(overviewMap.get("resolved_items")));
        overview.setTotalUsers(toLong(overviewMap.get("total_users")));
        dto.setOverview(overview);

        // 2. 失物状态分布
        List<Map<String, Object>> lostStatusList = statisticsMapper.getLostStatusDistribution();
        List<StatisticsDTO.StatusDistribution> lostStatusDist = new ArrayList<>();
        for (Map<String, Object> row : lostStatusList) {
            StatisticsDTO.StatusDistribution sd = new StatisticsDTO.StatusDistribution();
            sd.setStatus(((Number) row.get("status")).intValue());
            sd.setStatusName((String) row.get("status_name"));
            sd.setCount(toLong(row.get("cnt")));
            lostStatusDist.add(sd);
        }
        dto.setLostStatusDistribution(lostStatusDist);

        // 3. 招领状态分布
        List<Map<String, Object>> foundStatusList = statisticsMapper.getFoundStatusDistribution();
        List<StatisticsDTO.StatusDistribution> foundStatusDist = new ArrayList<>();
        for (Map<String, Object> row : foundStatusList) {
            StatisticsDTO.StatusDistribution sd = new StatisticsDTO.StatusDistribution();
            sd.setStatus(((Number) row.get("status")).intValue());
            sd.setStatusName((String) row.get("status_name"));
            sd.setCount(toLong(row.get("cnt")));
            foundStatusDist.add(sd);
        }
        dto.setFoundStatusDistribution(foundStatusDist);

        // 4. 分类分布
        List<Map<String, Object>> catList = statisticsMapper.getCategoryDistribution();
        List<StatisticsDTO.CategoryDistribution> catDist = new ArrayList<>();
        for (Map<String, Object> row : catList) {
            StatisticsDTO.CategoryDistribution cd = new StatisticsDTO.CategoryDistribution();
            cd.setCategoryId(toLong(row.get("category_id")));
            cd.setCategoryName((String) row.get("category_name"));
            cd.setLostCount(toLong(row.get("lost_count")));
            cd.setFoundCount(toLong(row.get("found_count")));
            cd.setTotal(toLong(row.get("total")));
            catDist.add(cd);
        }
        dto.setCategoryDistribution(catDist);

        // 5. 近7天趋势
        List<Map<String, Object>> trendList = statisticsMapper.getWeeklyTrend();
        List<StatisticsDTO.DailyTrend> trends = new ArrayList<>();
        for (Map<String, Object> row : trendList) {
            StatisticsDTO.DailyTrend dt = new StatisticsDTO.DailyTrend();
            dt.setDate(row.get("date").toString());
            dt.setDayOfWeek((String) row.get("day_of_week"));
            dt.setLostCount(toLong(row.get("lost_count")));
            dt.setFoundCount(toLong(row.get("found_count")));
            trends.add(dt);
        }
        dto.setWeeklyTrend(trends);

        return dto;
    }
//用户首页动态
    public HomeFeedDTO getHomeFeed() {
        HomeFeedDTO dto = new HomeFeedDTO();

        // 1.概览统计
        Map<String, Object> overviewMap = statisticsMapper.getHomeOverviewStats();
        HomeFeedDTO.OverviewStats overview = new HomeFeedDTO.OverviewStats();
        overview.setPendingItems(toLong(overviewMap.get("pending_items")));
        overview.setWeeklyNewItems(toLong(overviewMap.get("weekly_new_items")));
        overview.setResolvedItems(toLong(overviewMap.get("resolved_items")));
        overview.setTotalUsers(toLong(overviewMap.get("total_users")));
        dto.setOverview(overview);

        // 2.最近动态列表
        List<Map<String, Object>> activityList = statisticsMapper.getRecentActivities();
        List<HomeFeedDTO.FeedItem> activities = new ArrayList<>();
        for (Map<String, Object> row : activityList) {
            HomeFeedDTO.FeedItem item = new HomeFeedDTO.FeedItem();
            item.setItemType((String) row.get("item_type"));
            item.setItemId(toLong(row.get("item_id")));
            item.setTitle((String) row.get("title"));
            item.setUserId(toLong(row.get("user_id")));
            item.setUsername((String) row.get("username"));
            item.setAvatar((String) row.get("avatar"));
            item.setLocation((String) row.get("location"));
            item.setCategoryName((String) row.get("category_name"));
            Object createTime = row.get("create_time");
            item.setCreateTime(createTime != null ? createTime.toString() : null);
            activities.add(item);
        }
        dto.setActivities(activities);

        // 3. 已解决轮播(归还好消息横幅)
        List<Map<String, Object>> resolvedList = statisticsMapper.getRecentlyResolved();
        List<HomeFeedDTO.ResolvedItem> resolvedItems = new ArrayList<>();
        for (Map<String, Object> row : resolvedList) {
            HomeFeedDTO.ResolvedItem ri = new HomeFeedDTO.ResolvedItem();
            ri.setItemType((String) row.get("item_type"));
            ri.setItemId(toLong(row.get("item_id")));
            ri.setTitle((String) row.get("title"));
            ri.setUsername((String) row.get("username"));
            Object resolvedTime = row.get("resolved_time");
            ri.setResolvedTime(resolvedTime != null ? resolvedTime.toString() : null);
            resolvedItems.add(ri);
        }
        dto.setResolvedBanner(resolvedItems);

        // 4.近7天趋势(本周热力图)
        List<Map<String, Object>> trendList = statisticsMapper.getWeeklyTrend();
        List<HomeFeedDTO.DailyTrend> trends = new ArrayList<>();
        for (Map<String, Object> row : trendList) {
            HomeFeedDTO.DailyTrend dt = new HomeFeedDTO.DailyTrend();
            dt.setDate(row.get("date").toString());
            dt.setDayOfWeek((String) row.get("day_of_week"));
            dt.setLostCount(toLong(row.get("lost_count")));
            dt.setFoundCount(toLong(row.get("found_count")));
            trends.add(dt);
        }
        dto.setWeeklyTrend(trends);

        return dto;
    }

    private long toLong(Object val) {
        if (val == null) return 0L;
        return ((Number) val).longValue();
    }
}
