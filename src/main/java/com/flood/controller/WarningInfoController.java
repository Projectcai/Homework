package com.flood.controller;

import com.flood.common.PageResult;
import com.flood.common.Result;
import com.flood.entity.WarningInfo;
import com.flood.service.WarningInfoService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
 * 预警信息控制器
 */
@RestController
@RequestMapping("/api/warning")
public class WarningInfoController {

    @Resource
    private WarningInfoService warningInfoService;

    /**
     * 分页查询预警信息
     */
    @GetMapping("/page")
    public Result<PageResult<WarningInfo>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer warningLevel) {
        PageResult<WarningInfo> result = warningInfoService.getWarningPage(pageNum, pageSize, keyword, warningLevel);
        return Result.success(result);
    }

    /**
     * 根据ID查询预警信息
     */
    @GetMapping("/{id}")
    public Result<WarningInfo> getById(@PathVariable Long id) {
        WarningInfo warning = warningInfoService.getWarningById(id);
        return Result.success(warning);
    }

    /**
     * 发布预警
     */
    @PostMapping
    public Result<Void> publish(@RequestBody WarningInfo warningInfo) {
        warningInfoService.publishWarning(warningInfo);
        return Result.success("预警发布成功", null);
    }

    /**
     * 更新预警
     */
    @PutMapping
    public Result<Void> update(@RequestBody WarningInfo warningInfo) {
        warningInfoService.updateWarning(warningInfo);
        return Result.success("更新成功", null);
    }

    /**
     * 删除预警
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        warningInfoService.deleteWarning(id);
        return Result.success("删除成功", null);
    }

    /**
     * 更新预警状态
     */
    @PutMapping("/status/{id}")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam int status) {
        warningInfoService.updateWarningStatus(id, status);
        return Result.success("状态更新成功", null);
    }
}
