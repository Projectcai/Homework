package com.flood.service;

import com.flood.common.PageResult;
import com.flood.entity.WarningInfo;

/**
 * 预警信息服务接口
 */
public interface WarningInfoService {

    /**
     * 分页查询预警信息
     */
    PageResult<WarningInfo> getWarningPage(int pageNum, int pageSize, String keyword, Integer warningLevel);

    /**
     * 根据ID查询预警信息
     */
    WarningInfo getWarningById(Long id);

    /**
     * 发布预警
     */
    boolean publishWarning(WarningInfo warningInfo);

    /**
     * 更新预警
     */
    boolean updateWarning(WarningInfo warningInfo);

    /**
     * 删除预警
     */
    boolean deleteWarning(Long id);

    /**
     * 更新预警状态
     */
    boolean updateWarningStatus(Long id, int status);
}
