package com.flood.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flood.entity.TeamSkill;
import com.flood.mapper.TeamSkillMapper;
import com.flood.service.TeamSkillService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 队伍技能服务实现 (F)
 */
@Service
public class TeamSkillServiceImpl implements TeamSkillService {

    @Resource
    private TeamSkillMapper teamSkillMapper;

    @Override
    public List<TeamSkill> listByTeamId(Long teamId) {
        return teamSkillMapper.selectByTeamId(teamId);
    }

    @Override
    public List<TeamSkill> listAll() {
        return teamSkillMapper.selectList(new LambdaQueryWrapper<TeamSkill>()
                .orderByDesc(TeamSkill::getCreateTime));
    }

    @Override
    public TeamSkill getById(Long id) {
        return teamSkillMapper.selectById(id);
    }

    @Override
    @Transactional
    public boolean add(TeamSkill teamSkill) {
        teamSkill.setCreateTime(new Date());
        return teamSkillMapper.insert(teamSkill) > 0;
    }

    @Override
    @Transactional
    public boolean update(TeamSkill teamSkill) {
        return teamSkillMapper.updateById(teamSkill) > 0;
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        return teamSkillMapper.deleteById(id) > 0;
    }
}
