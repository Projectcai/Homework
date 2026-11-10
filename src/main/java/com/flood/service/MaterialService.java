package com.flood.service;

import com.flood.common.PageResult;
import com.flood.dto.MaterialQueryDTO;
import com.flood.entity.Material;

import java.util.List;

/**
 * 防汛物资服务接口
 */
public interface MaterialService {

    /**
     * 分页查询物资
     */
    PageResult<Material> getMaterialPage(int pageNum, int pageSize, MaterialQueryDTO queryDTO);

    /**
     * 根据ID查询物资
     */
    Material getMaterialById(Long id);

    /**
     * 添加物资
     */
    boolean addMaterial(Material material);

    /**
     * 更新物资
     */
    boolean updateMaterial(Material material);

    /**
     * 删除物资
     */
    boolean deleteMaterial(Long id);

    /**
     * 查询低库存物资
     */
    List<Material> getLowStockMaterials();

    /**
     * 入库
     */
    boolean inbound(Long id, int quantity);

    /**
     * 出库
     */
    boolean outbound(Long id, int quantity);
}
