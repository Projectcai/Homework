package com.flood.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flood.common.PageResult;
import com.flood.entity.DispatchPlan;
import com.flood.entity.EmergencyTeam;
import com.flood.entity.Material;
import com.flood.mapper.DispatchPlanMapper;
import com.flood.mapper.EmergencyTeamMapper;
import com.flood.mapper.MaterialMapper;
import com.flood.service.DispatchPlanService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 调度计划服务实现 - 优化版: 道路评分(G) + Haversine距离(L) + 生成后更新队伍状态(D)
 */
@Service
public class DispatchPlanServiceImpl implements DispatchPlanService {

    @Resource
    private DispatchPlanMapper dispatchPlanMapper;

    @Resource
    private EmergencyTeamMapper emergencyTeamMapper;

    @Resource
    private MaterialMapper materialMapper;

    /** 调度后用于Haversine距离计算的模拟受灾点坐标 (L) */
    private static final double DISASTER_LNG = 116.3980;
    private static final double DISASTER_LAT = 39.9095;

    @Override
    public PageResult<DispatchPlan> getDispatchPage(int pageNum, int pageSize, String keyword, Integer status) {
        Page<DispatchPlan> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<DispatchPlan> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(DispatchPlan::getPlanName, keyword)
                    .or().like(DispatchPlan::getDisasterLocation, keyword));
        }
        if (status != null) {
            wrapper.eq(DispatchPlan::getStatus, status);
        }
        wrapper.orderByDesc(DispatchPlan::getCreateTime);
        dispatchPlanMapper.selectPage(page, wrapper);
        List<DispatchPlan> records = page.getRecords();
        for (DispatchPlan dp : records) {
            fillDetailNames(dp);
        }
        return new PageResult<>(page.getTotal(), records, pageNum, pageSize);
    }

    @Override
    public DispatchPlan getDispatchById(Long id) {
        DispatchPlan plan = dispatchPlanMapper.selectById(id);
        if (plan != null) fillDetailNames(plan);
        return plan;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createDispatch(DispatchPlan dispatchPlan) {
        dispatchPlan.setCreateTime(new Date());
        dispatchPlan.setUpdateTime(new Date());
        if (dispatchPlan.getStatus() == null) dispatchPlan.setStatus(0);
        return dispatchPlanMapper.insert(dispatchPlan) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDispatch(DispatchPlan dispatchPlan) {
        dispatchPlan.setUpdateTime(new Date());
        return dispatchPlanMapper.updateById(dispatchPlan) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDispatchStatus(Long id, int status) {
        DispatchPlan plan = dispatchPlanMapper.selectById(id);
        if (plan == null) throw new RuntimeException("调度计划不存在");
        plan.setStatus(status);
        plan.setUpdateTime(new Date());
        return dispatchPlanMapper.updateById(plan) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDispatch(Long id) {
        return dispatchPlanMapper.deleteById(id) > 0;
    }

    /**
     * 智能调度生成 - 优化版: 道路评分(G) + Haversine距离(L) + 生成后更新队伍状态(D)
     * 权重: 距离评分(40%) + 队伍人数评分(25%) + 装备匹配度评分(25%) + 道路评分(10%)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DispatchPlan generateDispatchPlan(String disasterLocation, Integer disasterLevel,
                                             Long materialId, int neededQuantity) {
        // 1. 校验物资库存
        Material material = materialMapper.selectById(materialId);
        if (material == null) throw new RuntimeException("物资不存在");
        if (material.getQuantity() < neededQuantity) {
            throw new RuntimeException("物资库存不足，当前库存: " + material.getQuantity()
                    + " " + material.getUnit() + "，需求量: " + neededQuantity);
        }

        // 2. 获取可调度队伍 (status=0休整 或 1待命)
        LambdaQueryWrapper<EmergencyTeam> teamWrapper = new LambdaQueryWrapper<>();
        teamWrapper.in(EmergencyTeam::getStatus, 0, 1);
        List<EmergencyTeam> teams = emergencyTeamMapper.selectList(teamWrapper);
        if (teams.isEmpty()) throw new RuntimeException("当前没有可调度的应急队伍");

        // 3. 每支队伍综合评分
        List<TeamScore> scores = new ArrayList<>();
        for (EmergencyTeam team : teams) {
            double totalScore = calculateScore(team, disasterLocation, disasterLevel);
            scores.add(new TeamScore(team, totalScore));
        }

        // 4. 排序取最高分
        scores.sort((a, b) -> Double.compare(b.score, a.score));
        EmergencyTeam bestTeam = scores.get(0).team;

        // 5. 生成调度单
        DispatchPlan plan = new DispatchPlan();
        plan.setPlanName(disasterLocation + disasterLevelToText(disasterLevel) + "应急调度计划");
        plan.setDisasterLocation(disasterLocation);
        plan.setDisasterLevel(disasterLevel);
        plan.setMaterialId(materialId);
        plan.setMaterialQuantity(neededQuantity);
        plan.setTeamId(bestTeam.getId());
        plan.setDispatchTime(new Date());
        plan.setStatus(0);
        plan.setRemark("智能调度生成: 推荐队伍[" + bestTeam.getTeamName() + "]，"
                + "综合评分: " + String.format("%.2f", scores.get(0).score) + "分"
                + " (距离40% + 人数25% + 装备25% + 道路10%)");
        plan.setCreateTime(new Date());
        plan.setUpdateTime(new Date());
        dispatchPlanMapper.insert(plan);

        // D: 调度生成后, 将选中队伍状态更新为"出动中"(status=2)
        bestTeam.setStatus(2);
        bestTeam.setUpdateTime(new Date());
        emergencyTeamMapper.updateById(bestTeam);

        plan.setMaterialName(material.getMaterialName());
        plan.setTeamName(bestTeam.getTeamName());
        return plan;
    }

    /** 综合评分: 距离40% + 人数25% + 装备25% + 道路10% (G+L) */
    private double calculateScore(EmergencyTeam team, String disasterLocation, Integer disasterLevel) {
        // 距离评分(40%) - Haversine直线距离 (L), 无经纬度时退化到地址相似度
        double distanceScore;
        if (team.getLongitude() != null && team.getLatitude() != null) {
            double distKm = haversineKm(team.getLatitude(), team.getLongitude(), DISASTER_LAT, DISASTER_LNG);
            distanceScore = Math.max(0, 100 - distKm * 5); // 20km以上得0分
        } else {
            distanceScore = calculateLocationSimilarity(team.getLocation(), disasterLocation) * 100;
        }

        // 人数评分(25%)
        int memberCount = team.getMemberCount() != null ? team.getMemberCount() : 0;
        double memberScore = Math.min((double) memberCount / 100, 1.0) * 100;

        // 装备评分(25%)
        double equipmentScore = calculateEquipmentScore(team.getEquipment(), disasterLevel);

        // 道路评分(10%) (G)
        double roadScore = calculateRoadScore(team.getRoadCondition());

        return distanceScore * 0.40 + memberScore * 0.25 + equipmentScore * 0.25 + roadScore * 0.10;
    }

    /** Haversine 公式计算两点间距离 (km) (L) */
    private double haversineKm(double lat1, double lng1, double lat2, double lng2) {
        double R = 6371.0; // 地球半径 km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    /** 道路状况评分 (G) */
    private double calculateRoadScore(String roadCondition) {
        if (roadCondition == null) return 50;
        switch (roadCondition) {
            case "良好": return 100;
            case "一般": return 60;
            case "较差": return 20;
            default:     return 50;
        }
    }

    /** 地址相似度 (退化方案) */
    private double calculateLocationSimilarity(String teamLocation, String disasterLocation) {
        if (teamLocation == null || disasterLocation == null) return 0.0;
        if (teamLocation.equals(disasterLocation)) return 1.0;
        if (teamLocation.contains(disasterLocation) || disasterLocation.contains(teamLocation)) return 0.8;
        return calculateStringSimilarity(teamLocation, disasterLocation);
    }

    private double calculateStringSimilarity(String s1, String s2) {
        if (s1 == null || s2 == null || s1.isEmpty() || s2.isEmpty()) return 0.0;
        Set<Character> set1 = s1.chars().mapToObj(c -> (char) c).collect(Collectors.toSet());
        Set<Character> set2 = s2.chars().mapToObj(c -> (char) c).collect(Collectors.toSet());
        Set<Character> inter = new HashSet<>(set1);
        inter.retainAll(set2);
        Set<Character> union = new HashSet<>(set1);
        union.addAll(set2);
        return union.isEmpty() ? 0.0 : (double) inter.size() / union.size();
    }

    private double calculateEquipmentScore(String equipment, Integer disasterLevel) {
        double baseScore = 50;
        if (equipment != null && !equipment.trim().isEmpty()) {
            baseScore = Math.min(equipment.length() * 2.0, 100);
        }
        if (disasterLevel != null && disasterLevel >= 3) {
            baseScore = baseScore * 0.8;
        }
        return baseScore;
    }

    private String disasterLevelToText(Integer disasterLevel) {
        if (disasterLevel == null) return "";
        switch (disasterLevel) {
            case 1: return "一般";
            case 2: return "较大";
            case 3: return "重大";
            case 4: return "特大";
            default: return "";
        }
    }

    private void fillDetailNames(DispatchPlan dp) {
        if (dp.getMaterialId() != null) {
            Material m = materialMapper.selectById(dp.getMaterialId());
            if (m != null) dp.setMaterialName(m.getMaterialName());
        }
        if (dp.getTeamId() != null) {
            EmergencyTeam t = emergencyTeamMapper.selectById(dp.getTeamId());
            if (t != null) dp.setTeamName(t.getTeamName());
        }
    }

    private static class TeamScore {
        EmergencyTeam team;
        double score;
        TeamScore(EmergencyTeam team, double score) {
            this.team = team;
            this.score = score;
        }
    }
}
