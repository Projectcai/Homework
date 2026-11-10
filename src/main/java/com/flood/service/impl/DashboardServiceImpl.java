package com.flood.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flood.entity.DispatchPlan;
import com.flood.entity.EmergencyTeam;
import com.flood.entity.Material;
import com.flood.entity.WarningInfo;
import com.flood.mapper.DispatchPlanMapper;
import com.flood.mapper.EmergencyTeamMapper;
import com.flood.mapper.MaterialMapper;
import com.flood.mapper.WarningInfoMapper;
import com.flood.service.DashboardService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 仪表盘服务实现类
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    @Resource
    private MaterialMapper materialMapper;

    @Resource
    private EmergencyTeamMapper emergencyTeamMapper;

    @Resource
    private WarningInfoMapper warningInfoMapper;

    @Resource
    private DispatchPlanMapper dispatchPlanMapper;

    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // 物资总数
        Long materialTotalQuantity = 0L;
        List<Material> materials = materialMapper.selectList(null);
        for (Material m : materials) {
            if (m.getQuantity() != null) {
                materialTotalQuantity += m.getQuantity();
            }
        }
        stats.put("materialCount", materials.size());
        stats.put("materialTotalQuantity", materialTotalQuantity);

        // 低库存物资数
        List<Material> lowStockMaterials = materialMapper.selectLowStockMaterials();
        stats.put("lowStockCount", lowStockMaterials.size());

        // 队伍总数
        Long teamCount = emergencyTeamMapper.selectCount(null);
        stats.put("teamCount", teamCount);

        // 待命队伍数
        LambdaQueryWrapper<EmergencyTeam> teamWrapper = new LambdaQueryWrapper<>();
        teamWrapper.eq(EmergencyTeam::getStatus, 1);
        Long standbyTeamCount = emergencyTeamMapper.selectCount(teamWrapper);
        stats.put("standbyTeamCount", standbyTeamCount);

        // 预警数（已发布）
        LambdaQueryWrapper<WarningInfo> warningWrapper = new LambdaQueryWrapper<>();
        warningWrapper.eq(WarningInfo::getStatus, 1);
        Long warningCount = warningInfoMapper.selectCount(warningWrapper);
        stats.put("warningCount", warningCount);

        // 调度计划数
        Long dispatchCount = dispatchPlanMapper.selectCount(null);
        stats.put("dispatchCount", dispatchCount);

        // 待执行调度数
        LambdaQueryWrapper<DispatchPlan> dispatchWrapper = new LambdaQueryWrapper<>();
        dispatchWrapper.eq(DispatchPlan::getStatus, 0);
        Long pendingDispatchCount = dispatchPlanMapper.selectCount(dispatchWrapper);
        stats.put("pendingDispatchCount", pendingDispatchCount);

        return stats;
    }

    @Override
    public Map<String, Object> getMaterialCategoryStats() {
        List<Material> materials = materialMapper.selectList(null);
        Map<String, Integer> sumMap = new LinkedHashMap<>();
        for (Material m : materials) {
            String type = m.getMaterialType() == null || m.getMaterialType().isEmpty()
                    ? "未分类" : m.getMaterialType();
            sumMap.merge(type, m.getQuantity() == null ? 0 : m.getQuantity(), Integer::sum);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("categories", new ArrayList<>(sumMap.keySet()));
        result.put("values", new ArrayList<>(sumMap.values()));
        return result;
    }

    @Override
    public Map<String, Object> getWarningTrend() {
        List<WarningInfo> warnings = warningInfoMapper.selectList(null);
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        List<String> dates = new ArrayList<>();
        List<Integer> values = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate day = today.minusDays(i);
            dates.add(day.format(formatter));
            int count = 0;
            for (WarningInfo w : warnings) {
                if (w.getPublishTime() != null) {
                    LocalDate publishDay = w.getPublishTime().toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalDate();
                    if (publishDay.equals(day)) {
                        count++;
                    }
                }
            }
            values.add(count);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("dates", dates);
        result.put("values", values);
        return result;
    }
    @Override
    public Map<String, Object> getDispatchStats() {
        Map<String, Object> result = new HashMap<>();
        int pending = dispatchPlanMapper.selectCount(
                new LambdaQueryWrapper<DispatchPlan>().eq(DispatchPlan::getStatus, 0)).intValue();
        int running = dispatchPlanMapper.selectCount(
                new LambdaQueryWrapper<DispatchPlan>().eq(DispatchPlan::getStatus, 1)).intValue();
        int done = dispatchPlanMapper.selectCount(
                new LambdaQueryWrapper<DispatchPlan>().eq(DispatchPlan::getStatus, 2)).intValue();
        int cancelled = dispatchPlanMapper.selectCount(
                new LambdaQueryWrapper<DispatchPlan>().eq(DispatchPlan::getStatus, 3)).intValue();

        List<String> labels = List.of("待执行", "执行中", "已完成", "已取消");
        List<Integer> values = List.of(pending, running, done, cancelled);
        result.put("labels", labels);
        result.put("values", values);
        result.put("total", pending + running + done + cancelled);
        return result;
    }

}
