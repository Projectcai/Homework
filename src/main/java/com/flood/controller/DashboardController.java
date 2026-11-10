package com.flood.controller;

import com.flood.common.Result;
import com.flood.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.Map;

/**
 * 仪表盘控制器
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Resource
    private DashboardService dashboardService;

    /**
     * 获取统计数据
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> statistics() {
        Map<String, Object> stats = dashboardService.getStatistics();
        return Result.success(stats);
    }

    /**
     * 获取物资分类统计(柱状图)
     */
    @GetMapping("/material-category")
    public Result<Map<String, Object>> materialCategory() {
        return Result.success(dashboardService.getMaterialCategoryStats());
    }

    /**
     * 获取预警趋势统计(折线图)
     */
    @GetMapping("/warning-trend")
    public Result<Map<String, Object>> warningTrend() {
        return Result.success(dashboardService.getWarningTrend());
    }
    /**
     * 调度统计图表接口 (A) - 按状态分组
     */
    @GetMapping("/dispatch-stats")
    public Result<Map<String, Object>> dispatchStats() {
        return Result.success(dashboardService.getDispatchStats());
    }

}
