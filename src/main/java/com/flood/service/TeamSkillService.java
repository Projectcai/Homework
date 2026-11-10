package com.flood.service;

import com.flood.entity.TeamSkill;

import java.util.List;

/**
 * 队伍技能服务 (F)
 */
public interface TeamSkillService {
    List<TeamSkill> listByTeamId(Long teamId);
    List<TeamSkill> listAll();
    TeamSkill getById(Long id);
    boolean add(TeamSkill teamSkill);
    boolean update(TeamSkill teamSkill);
    boolean delete(Long id);
}
