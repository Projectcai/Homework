package com.flood.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flood.common.PageResult;
import com.flood.entity.OperationLog;
import com.flood.mapper.OperationLogMapper;
import com.flood.service.OperationLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.Date;

/**
 * 操作日志服务实现类
 */
@Service
public class OperationLogServiceImpl implements OperationLogService {

    @Resource
    private OperationLogMapper operationLogMapper;

    @Override
    public PageResult<OperationLog> getLogPage(int pageNum, int pageSize, String keyword) {
        Page<OperationLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(OperationLog::getUsername, keyword)
                    .or().like(OperationLog::getOperation, keyword)
                    .or().like(OperationLog::getMethod, keyword);
        }
        wrapper.orderByDesc(OperationLog::getCreateTime);
        operationLogMapper.selectPage(page, wrapper);
        return new PageResult<>(page.getTotal(), page.getRecords(), pageNum, pageSize);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addLog(OperationLog operationLog) {
        operationLog.setCreateTime(new Date());
        return operationLogMapper.insert(operationLog) > 0;
    }
}
