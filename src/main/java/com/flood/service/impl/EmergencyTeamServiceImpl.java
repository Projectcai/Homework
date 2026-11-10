package com.flood.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flood.common.PageResult;
import com.flood.entity.EmergencyTeam;
import com.flood.mapper.EmergencyTeamMapper;
import com.flood.service.EmergencyTeamService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 应急队伍服务实现类
 */
@Service
public class EmergencyTeamServiceImpl implements EmergencyTeamService {

    @Resource
    private EmergencyTeamMapper emergencyTeamMapper;

    @Override
    public List<EmergencyTeam> getAllTeams() {
        LambdaQueryWrapper<EmergencyTeam> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(EmergencyTeam::getStatus).orderByDesc(EmergencyTeam::getCreateTime);
        return emergencyTeamMapper.selectList(wrapper);
    }

    @Override
    public PageResult<EmergencyTeam> getTeamPage(int pageNum, int pageSize, String keyword) {
        Page<EmergencyTeam> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<EmergencyTeam> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(EmergencyTeam::getTeamName, keyword)
                    .or().like(EmergencyTeam::getLeaderName, keyword)
                    .or().like(EmergencyTeam::getLocation, keyword);
        }
        wrapper.orderByDesc(EmergencyTeam::getCreateTime);
        emergencyTeamMapper.selectPage(page, wrapper);
        return new PageResult<>(page.getTotal(), page.getRecords(), pageNum, pageSize);
    }

    @Override
    public EmergencyTeam getTeamById(Long id) {
        return emergencyTeamMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addTeam(EmergencyTeam team) {
        team.setCreateTime(new Date());
        team.setUpdateTime(new Date());
        if (team.getStatus() == null) {
            team.setStatus(1);
        }
        return emergencyTeamMapper.insert(team) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTeam(EmergencyTeam team) {
        team.setUpdateTime(new Date());
        return emergencyTeamMapper.updateById(team) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeam(Long id) {
        return emergencyTeamMapper.deleteById(id) > 0;
    }
}
