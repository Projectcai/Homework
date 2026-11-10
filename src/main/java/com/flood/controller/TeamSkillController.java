package com.flood.controller;

import com.flood.common.Result;
import com.flood.entity.TeamSkill;
import com.flood.service.TeamSkillService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 队伍技能控制器 (F)
 */
@RestController
@RequestMapping("/api/team-skill")
public class TeamSkillController {

    @Resource
    private TeamSkillService teamSkillService;

    @GetMapping("/list")
    public Result<List<TeamSkill>> list() {
        return Result.success(teamSkillService.listAll());
    }

    @GetMapping("/team/{teamId}")
    public Result<List<TeamSkill>> listByTeam(@PathVariable Long teamId) {
        return Result.success(teamSkillService.listByTeamId(teamId));
    }

    @GetMapping("/{id}")
    public Result<TeamSkill> getById(@PathVariable Long id) {
        return Result.success(teamSkillService.getById(id));
    }

    @PostMapping
    public Result<Void> add(@RequestBody TeamSkill teamSkill) {
        teamSkillService.add(teamSkill);
        return Result.success("添加成功", null);
    }

    @PutMapping
    public Result<Void> update(@RequestBody TeamSkill teamSkill) {
        teamSkillService.update(teamSkill);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        teamSkillService.delete(id);
        return Result.success("删除成功", null);
    }
}
