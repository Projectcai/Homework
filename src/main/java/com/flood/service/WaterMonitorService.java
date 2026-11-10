package com.flood.service;

import java.util.List;
import java.util.Map;

/**
 * 水文监测服务接口 (J - 可接外部API, 目前Mock实现)
 */
public interface WaterMonitorService {

    /** 获取当前水位数据 (米) */
    Map<String, Object> getCurrentWaterLevel(String stationCode);

    /** 获取当前降雨量 (毫米) */
    Map<String, Object> getCurrentRainfall(String stationCode);

    /** 获取所有监测站最新数据 */
    List<Map<String, Object>> getAllStationsData();

    /** 判断是否超过预警阈值 */
    boolean isAboveThreshold(String stationCode, Double threshold);
}
