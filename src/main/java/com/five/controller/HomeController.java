package com.five.controller;

import com.five.dto.HomeFeedDTO;
import com.five.service.StatisticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/home", "/api/home"})
public class HomeController {
    //负责数据处理 用来给前端首页提供动态信息 ，可以把失物招领或者寻妻启事等多条动态合并成一条时间线展示出来
    private final StatisticsService statisticsService;

    public HomeController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }
   //获取首页动态
    @GetMapping("/feed")
    public HomeFeedDTO getHomeFeed() {
        return statisticsService.getHomeFeed();
    }
}
