package com.flood.controller;

import com.flood.common.Result;
import com.flood.service.WaterMonitorService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 水文监测控制器 (J)
 * 当前由 Mock 服务提供数据, 后续可直接替换为对接水文部门 API
 */
@RestController
@RequestMapping("/api/water-monitor")
public class WaterMonitorController {

    @Resource
    private WaterMonitorService waterMonitorService;

    /** 所有监测站最新数据 */
    @GetMapping("/stations")
    public Result<List<Map<String, Object>>> stations() {
        return Result.success(waterMonitorService.getAllStationsData());
    }

    /** 指定监测站水位数据 */
    @GetMapping("/water-level/{stationCode}")
    public Result<Map<String, Object>> waterLevel(@PathVariable String stationCode) {
        return Result.success(waterMonitorService.getCurrentWaterLevel(stationCode));
    }

    /** 指定监测站降雨量数据 */
    @GetMapping("/rainfall/{stationCode}")
    public Result<Map<String, Object>> rainfall(@PathVariable String stationCode) {
        return Result.success(waterMonitorService.getCurrentRainfall(stationCode));
    }
}
