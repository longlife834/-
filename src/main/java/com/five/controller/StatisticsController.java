package com.five.controller;

import com.five.dto.StatisticsDTO;
import com.five.service.StatisticsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/admin", "/api/admin"})
@PreAuthorize("hasRole('ADMIN')")
public class StatisticsController {
    //聚合了用户，失物，招领等核心数据，提供后台可视化图标
    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/statistics")
    public StatisticsDTO getStatistics() {
        return statisticsService.getStatistics();
    }
}
