package com.flood.controller;

import com.flood.common.PageResult;
import com.flood.common.Result;
import com.flood.entity.OperationLog;
import com.flood.service.OperationLogService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
 * 操作日志控制器
 */
@RestController
@RequestMapping("/api/log")
public class OperationLogController {

    @Resource
    private OperationLogService operationLogService;

    /**
     * 分页查询操作日志
     */
    @GetMapping("/page")
    public Result<PageResult<OperationLog>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        PageResult<OperationLog> result = operationLogService.getLogPage(pageNum, pageSize, keyword);
        return Result.success(result);
    }
}
