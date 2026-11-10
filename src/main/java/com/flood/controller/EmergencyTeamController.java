package com.flood.controller;

import com.flood.common.PageResult;
import com.flood.common.Result;
import com.flood.entity.EmergencyTeam;
import com.flood.service.EmergencyTeamService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 应急队伍控制器
 */
@RestController
@RequestMapping("/api/team")
public class EmergencyTeamController {

    @Resource
    private EmergencyTeamService emergencyTeamService;

    /**
     * 查询全部队伍
     */
    @GetMapping("/all")
    public Result<List<EmergencyTeam>> all() {
        return Result.success(emergencyTeamService.getAllTeams());
    }

    /**
     * 分页查询队伍
     */
    @GetMapping("/page")
    public Result<PageResult<EmergencyTeam>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        PageResult<EmergencyTeam> result = emergencyTeamService.getTeamPage(pageNum, pageSize, keyword);
        return Result.success(result);
    }

    /**
     * 根据ID查询队伍
     */
    @GetMapping("/{id}")
    public Result<EmergencyTeam> getById(@PathVariable Long id) {
        EmergencyTeam team = emergencyTeamService.getTeamById(id);
        return Result.success(team);
    }

    /**
     * 添加队伍
     */
    @PostMapping
    public Result<Void> add(@RequestBody EmergencyTeam team) {
        emergencyTeamService.addTeam(team);
        return Result.success("添加成功", null);
    }

    /**
     * 更新队伍
     */
    @PutMapping
    public Result<Void> update(@RequestBody EmergencyTeam team) {
        emergencyTeamService.updateTeam(team);
        return Result.success("更新成功", null);
    }

    /**
     * 删除队伍
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        emergencyTeamService.deleteTeam(id);
        return Result.success("删除成功", null);
    }
}
