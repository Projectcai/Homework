package com.flood.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flood.entity.TeamSkill;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 队伍技能Mapper (F)
 */
@Mapper
public interface TeamSkillMapper extends BaseMapper<TeamSkill> {

    @Select("SELECT * FROM biz_team_skill WHERE team_id = #{teamId}")
    List<TeamSkill> selectByTeamId(@Param("teamId") Long teamId);
}
