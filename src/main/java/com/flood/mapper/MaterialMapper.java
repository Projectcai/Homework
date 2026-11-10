package com.flood.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flood.entity.Material;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 防汛物资Mapper
 */
@Mapper
public interface MaterialMapper extends BaseMapper<Material> {

    /**
     * 查询库存低于预警值的物资
     */
    List<Material> selectLowStockMaterials();
}
