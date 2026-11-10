package com.flood.service;

import java.util.Map;

/**
 * 仪表盘服务接口
 */
public interface DashboardService {

    /**
     * 获取统计数据
     *
     * @return 统计数据Map
     */
    Map<String, Object> getStatistics();

    /**
     * 获取物资分类统计(柱状图数据)
     */
    Map<String, Object> getMaterialCategoryStats();

    /**
     * 获取预警趋势统计(折线图数据)
     */
    Map<String, Object> getWarningTrend();
    /** 调度统计 (A) */
    Map<String, Object> getDispatchStats();
}
