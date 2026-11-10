package com.flood.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flood.entity.DispatchPlan;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 调度计划Mapper
 */
@Mapper
public interface DispatchPlanMapper extends BaseMapper<DispatchPlan> {

    /**
     * 查询调度列表（关联物资和队伍名称）
     */
    List<DispatchPlan> selectDispatchListWithDetail();
}
