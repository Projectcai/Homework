package com.flood.service;

import com.flood.common.PageResult;
import com.flood.entity.EmergencyTeam;

import java.util.List;

/**
 * 应急队伍服务接口
 */
public interface EmergencyTeamService {

    /**
     * 查询全部队伍(供下拉选择使用)
     */
    List<EmergencyTeam> getAllTeams();

    /**
     * 分页查询队伍
     */
    PageResult<EmergencyTeam> getTeamPage(int pageNum, int pageSize, String keyword);

    /**
     * 根据ID查询队伍
     */
    EmergencyTeam getTeamById(Long id);

    /**
     * 添加队伍
     */
    boolean addTeam(EmergencyTeam team);

    /**
     * 更新队伍
     */
    boolean updateTeam(EmergencyTeam team);

    /**
     * 删除队伍
     */
    boolean deleteTeam(Long id);
}
