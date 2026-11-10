package com.flood.service;

import com.flood.common.PageResult;
import com.flood.entity.OperationLog;

/**
 * 操作日志服务接口
 */
public interface OperationLogService {

    /**
     * 分页查询操作日志
     */
    PageResult<OperationLog> getLogPage(int pageNum, int pageSize, String keyword);

    /**
     * 添加操作日志
     */
    boolean addLog(OperationLog operationLog);
}
