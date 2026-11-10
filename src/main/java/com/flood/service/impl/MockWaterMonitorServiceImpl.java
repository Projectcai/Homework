package com.flood.service.impl;

import com.flood.service.WaterMonitorService;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 水文监测服务Mock实现 (J)
 * 真实项目可替换为对接水文部门API
 */
@Service
public class MockWaterMonitorServiceImpl implements WaterMonitorService {

    // 模拟监测站数据: stationCode -> {水位, 降雨量, 阈值}
    private static final Map<String, Map<String, Object>> STATIONS = new HashMap<>();
    static {
        Map<String, Object> s1 = new HashMap<>();
        s1.put("stationCode", "ST001");
        s1.put("stationName", "XX镇沿河监测站");
        s1.put("waterLevel", 3.2);    // 米
        s1.put("rainfall", 45.0);      // 毫米
        s1.put("waterThreshold", 3.0); // 预警水位
        s1.put("rainThreshold", 50.0); // 预警降雨量
        s1.put("updateTime", new Date());
        STATIONS.put("ST001", s1);

        Map<String, Object> s2 = new HashMap<>();
        s2.put("stationCode", "ST002");
        s2.put("stationName", "XX水库监测站");
        s2.put("waterLevel", 12.5);
        s2.put("rainfall", 32.0);
        s2.put("waterThreshold", 13.0);
        s2.put("rainThreshold", 50.0);
        s2.put("updateTime", new Date());
        STATIONS.put("ST002", s2);
    }

    @Override
    public Map<String, Object> getCurrentWaterLevel(String stationCode) {
        Map<String, Object> s = STATIONS.get(stationCode);
        if (s == null) {
            Map<String, Object> empty = new HashMap<>();
            empty.put("stationCode", stationCode);
            empty.put("waterLevel", 0.0);
            empty.put("waterThreshold", 3.0);
            return empty;
        }
        return s;
    }

    @Override
    public Map<String, Object> getCurrentRainfall(String stationCode) {
        return getCurrentWaterLevel(stationCode);
    }

    @Override
    public List<Map<String, Object>> getAllStationsData() {
        return new ArrayList<>(STATIONS.values());
    }

    @Override
    public boolean isAboveThreshold(String stationCode, Double threshold) {
        if (threshold == null) return false;
        Map<String, Object> s = STATIONS.get(stationCode);
        if (s == null) return false;
        Double level = (Double) s.get("waterLevel");
        return level != null && level >= threshold;
    }
}
