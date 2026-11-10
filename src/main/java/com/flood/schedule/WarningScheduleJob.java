package com.flood.schedule;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flood.entity.Material;
import com.flood.entity.Notification;
import com.flood.entity.WarningInfo;
import com.flood.mapper.MaterialMapper;
import com.flood.mapper.NotificationMapper;
import com.flood.mapper.WarningInfoMapper;
import com.flood.service.WaterMonitorService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 定时预警任务
 * I: 低库存自动生成预警 + 水文监测阈值自动触发预警
 */
@Slf4j
@Component
public class WarningScheduleJob {

    @Resource
    private MaterialMapper materialMapper;

    @Resource
    private WarningInfoMapper warningInfoMapper;

    @Resource
    private NotificationMapper notificationMapper;

    @Resource
    private WaterMonitorService waterMonitorService;

    /**
     * 每5分钟检查一次低库存物资, 自动生成预警
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void checkLowStockAndCreateWarning() {
        List<Material> lowStock = materialMapper.selectLowStockMaterials();
        if (lowStock == null || lowStock.isEmpty()) return;

        for (Material m : lowStock) {
            if (hasActiveWarning(m.getMaterialName(), "物资库存")) {
                continue;
            }
            WarningInfo w = new WarningInfo();
            w.setTitle("低库存预警: " + m.getMaterialName());
            w.setWarningType("物资库存");
            w.setWarningLevel(1);
            w.setContent("物资[" + m.getMaterialName() + "]库存"
                    + m.getQuantity() + m.getUnit() + " 低于预警值"
                    + m.getWarningValue() + m.getUnit() + "，请及时补货。");
            w.setAffectedArea(m.getWarehouseLocation());
            w.setPublishTime(new Date());
            w.setStatus(1);
            w.setCreateTime(new Date());
            w.setUpdateTime(new Date());
            warningInfoMapper.insert(w);
            pushNotification(w.getTitle(), w.getContent(), "WARNING");
            log.info("[定时任务] 自动生成低库存预警: {}", w.getTitle());
        }
    }

    /**
     * 每10分钟检查一次水文监测数据, 超过物资设定阈值时自动生成预警
     */
    @Scheduled(cron = "0 */10 * * * ?")
    public void checkWaterLevelAndCreateWarning() {
        List<Map<String, Object>> stations = waterMonitorService.getAllStationsData();
        if (stations == null || stations.isEmpty()) return;

        // 取出所有设置了水文监测阈值的物资作为阈值参考
        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNotNull(Material::getMonitorThreshold);
        List<Material> thresholdMaterials = materialMapper.selectList(wrapper);
        if (thresholdMaterials.isEmpty()) return;

        for (Map<String, Object> station : stations) {
            String stationName = String.valueOf(station.get("stationName"));
            Object levelObj = station.get("waterLevel");
            if (levelObj == null) continue;
            double waterLevel = ((Number) levelObj).doubleValue();

            for (Material m : thresholdMaterials) {
                double threshold = m.getMonitorThreshold();
                if (waterLevel < threshold) continue;

                String key = stationName + "-" + m.getMaterialName();
                if (hasActiveWarning(key, "水位监测")) continue;

                WarningInfo w = new WarningInfo();
                w.setTitle("水位超限预警: " + stationName);
                w.setWarningType("水位监测");
                w.setWarningLevel(waterLevel >= threshold + 1.0 ? 3 : 2);
                w.setContent(stationName + "当前水位 " + waterLevel + " 米，已超过"
                        + m.getMaterialName() + "关联警戒阈值 " + threshold
                        + " 米，请启动应急响应。");
                w.setAffectedArea(stationName);
                w.setPublishTime(new Date());
                w.setStatus(1);
                w.setCreateTime(new Date());
                w.setUpdateTime(new Date());
                warningInfoMapper.insert(w);
                pushNotification(w.getTitle(), w.getContent(), "WARNING");
                log.info("[定时任务] 自动生成水位超限预警: {}", w.getTitle());
            }
        }
    }

    /**
     * 判断是否已有同类未解除预警, 避免重复生成
     */
    private boolean hasActiveWarning(String keyword, String warningType) {
        LambdaQueryWrapper<WarningInfo> existWrap = new LambdaQueryWrapper<>();
        existWrap.like(WarningInfo::getTitle, keyword)
                .eq(WarningInfo::getStatus, 1)
                .eq(WarningInfo::getWarningType, warningType);
        Long count = warningInfoMapper.selectCount(existWrap);
        return count != null && count > 0;
    }

    /**
     * K: 预警生成后写入站内通知
     */
    private void pushNotification(String title, String content, String type) {
        try {
            Notification n = new Notification();
            n.setTitle(title);
            n.setContent(content);
            n.setType(type);
            n.setStatus(0);
            n.setCreateTime(new Date());
            notificationMapper.insert(n);
        } catch (Exception e) {
            log.warn("推送站内通知失败: {}", e.getMessage());
        }
    }
}
