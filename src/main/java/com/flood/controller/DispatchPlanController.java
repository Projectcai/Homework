package com.flood.controller;

import com.flood.common.PageResult;
import com.flood.common.Result;
import com.flood.entity.DispatchPlan;
import com.flood.service.DispatchPlanService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
 * 调度计划控制器
 */
@RestController
@RequestMapping("/api/dispatch")
public class DispatchPlanController {

    @Resource
    private DispatchPlanService dispatchPlanService;

    /**
     * 分页查询调度计划
     */
    @GetMapping("/page")
    public Result<PageResult<DispatchPlan>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        PageResult<DispatchPlan> result = dispatchPlanService.getDispatchPage(pageNum, pageSize, keyword, status);
        return Result.success(result);
    }

    /**
     * 根据ID查询调度计划
     */
    @GetMapping("/{id}")
    public Result<DispatchPlan> getById(@PathVariable Long id) {
        DispatchPlan plan = dispatchPlanService.getDispatchById(id);
        return Result.success(plan);
    }

    /**
     * 创建调度计划
     */
    @PostMapping
    public Result<Void> create(@RequestBody DispatchPlan dispatchPlan) {
        dispatchPlanService.createDispatch(dispatchPlan);
        return Result.success("创建调度计划成功", null);
    }

    /**
     * 更新调度计划
     */
    @PutMapping
    public Result<Void> update(@RequestBody DispatchPlan dispatchPlan) {
        dispatchPlanService.updateDispatch(dispatchPlan);
        return Result.success("更新成功", null);
    }

    /**
     * 更新调度状态
     */
    @PutMapping("/status/{id}")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam int status) {
        dispatchPlanService.updateDispatchStatus(id, status);
        return Result.success("状态更新成功", null);
    }

    /**
     * 删除调度计划
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        dispatchPlanService.deleteDispatch(id);
        return Result.success("删除成功", null);
    }

    /**
     * 智能调度生成
     */
    @PostMapping("/generate")
    public Result<DispatchPlan> generate(
            @RequestParam String disasterLocation,
            @RequestParam Integer disasterLevel,
            @RequestParam Long materialId,
            @RequestParam int neededQuantity) {
        DispatchPlan plan = dispatchPlanService.generateDispatchPlan(
                disasterLocation, disasterLevel, materialId, neededQuantity);
        return Result.success("智能调度计划生成成功", plan);
    }
}
