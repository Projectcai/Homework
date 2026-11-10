package com.flood.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flood.common.PageResult;
import com.flood.dto.MaterialQueryDTO;
import com.flood.entity.Material;
import com.flood.mapper.MaterialMapper;
import com.flood.service.MaterialService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 防汛物资服务实现类
 */
@Service
public class MaterialServiceImpl implements MaterialService {

    @Resource
    private MaterialMapper materialMapper;

    @Override
    public PageResult<Material> getMaterialPage(int pageNum, int pageSize, MaterialQueryDTO queryDTO) {
        Page<Material> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();
        if (queryDTO != null) {
            if (queryDTO.getMaterialName() != null && !queryDTO.getMaterialName().trim().isEmpty()) {
                wrapper.like(Material::getMaterialName, queryDTO.getMaterialName());
            }
            if (queryDTO.getMaterialType() != null && !queryDTO.getMaterialType().trim().isEmpty()) {
                wrapper.eq(Material::getMaterialType, queryDTO.getMaterialType());
            }
            if (queryDTO.getStatus() != null) {
                wrapper.eq(Material::getStatus, queryDTO.getStatus());
            }
        }
        wrapper.orderByDesc(Material::getCreateTime);
        materialMapper.selectPage(page, wrapper);
        return new PageResult<>(page.getTotal(), page.getRecords(), pageNum, pageSize);
    }

    @Override
    public Material getMaterialById(Long id) {
        return materialMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addMaterial(Material material) {
        material.setCreateTime(new Date());
        material.setUpdateTime(new Date());
        if (material.getStatus() == null) {
            material.setStatus(1);
        }
        return materialMapper.insert(material) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMaterial(Material material) {
        material.setUpdateTime(new Date());
        return materialMapper.updateById(material) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteMaterial(Long id) {
        return materialMapper.deleteById(id) > 0;
    }

    @Override
    public List<Material> getLowStockMaterials() {
        return materialMapper.selectLowStockMaterials();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean inbound(Long id, int quantity) {
        if (quantity <= 0) {
            throw new RuntimeException("入库数量必须大于0");
        }
        Material material = materialMapper.selectById(id);
        if (material == null) {
            throw new RuntimeException("物资不存在");
        }
        material.setQuantity(material.getQuantity() + quantity);
        material.setUpdateTime(new Date());
        return materialMapper.updateById(material) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean outbound(Long id, int quantity) {
        if (quantity <= 0) {
            throw new RuntimeException("出库数量必须大于0");
        }
        Material material = materialMapper.selectById(id);
        if (material == null) {
            throw new RuntimeException("物资不存在");
        }
        if (material.getQuantity() < quantity) {
            throw new RuntimeException("库存不足，当前库存: " + material.getQuantity() + " " + material.getUnit());
        }
        material.setQuantity(material.getQuantity() - quantity);
        material.setUpdateTime(new Date());
        return materialMapper.updateById(material) > 0;
    }
}
