package com.flood.service;

import com.flood.common.PageResult;
import com.flood.entity.DispatchPlan;

/**
 * 调度计划服务接口
 */
public interface DispatchPlanService {

    /**
     * 分页查询调度计划
     */
    PageResult<DispatchPlan> getDispatchPage(int pageNum, int pageSize, String keyword, Integer status);

    /**
     * 根据ID查询调度计划
     */
    DispatchPlan getDispatchById(Long id);

    /**
     * 创建调度计划
     */
    boolean createDispatch(DispatchPlan dispatchPlan);

    /**
     * 更新调度计划
     */
    boolean updateDispatch(DispatchPlan dispatchPlan);

    /**
     * 更新调度状态
     */
    boolean updateDispatchStatus(Long id, int status);

    /**
     * 删除调度计划
     */
    boolean deleteDispatch(Long id);

    /**
     * 智能调度生成（就近分配+权重评分调度算法）
     *
     * @param disasterLocation 灾害地点
     * @param disasterLevel    灾害等级
     * @param materialId       物资ID
     * @param neededQuantity   所需数量
     * @return 生成的调度计划
     */
    DispatchPlan generateDispatchPlan(String disasterLocation, Integer disasterLevel, Long materialId, int neededQuantity);
}
