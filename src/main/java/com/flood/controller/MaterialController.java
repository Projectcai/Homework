package com.flood.controller;

import com.flood.common.PageResult;
import com.flood.common.Result;
import com.flood.dto.MaterialQueryDTO;
import com.flood.entity.Material;
import com.flood.service.MaterialService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 防汛物资控制器
 */
@RestController
@RequestMapping("/api/material")
public class MaterialController {

    @Resource
    private MaterialService materialService;

    /**
     * 分页查询物资
     */
    @GetMapping("/page")
    public Result<PageResult<Material>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String materialName,
            @RequestParam(required = false) String materialType,
            @RequestParam(required = false) Integer status) {
        MaterialQueryDTO queryDTO = new MaterialQueryDTO();
        queryDTO.setMaterialName(materialName);
        queryDTO.setMaterialType(materialType);
        queryDTO.setStatus(status);
        PageResult<Material> result = materialService.getMaterialPage(pageNum, pageSize, queryDTO);
        return Result.success(result);
    }

    /**
     * 根据ID查询物资
     */
    @GetMapping("/{id}")
    public Result<Material> getById(@PathVariable Long id) {
        Material material = materialService.getMaterialById(id);
        return Result.success(material);
    }

    /**
     * 添加物资
     */
    @PostMapping
    public Result<Void> add(@RequestBody Material material) {
        materialService.addMaterial(material);
        return Result.success("添加成功", null);
    }

    /**
     * 更新物资
     */
    @PutMapping
    public Result<Void> update(@RequestBody Material material) {
        materialService.updateMaterial(material);
        return Result.success("更新成功", null);
    }

    /**
     * 删除物资
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        materialService.deleteMaterial(id);
        return Result.success("删除成功", null);
    }

    /**
     * 查询低库存物资
     */
    @GetMapping("/low-stock")
    public Result<List<Material>> lowStock() {
        List<Material> materials = materialService.getLowStockMaterials();
        return Result.success(materials);
    }

    /**
     * 入库
     */
    @PutMapping("/inbound/{id}")
    public Result<Void> inbound(@PathVariable Long id, @RequestParam int quantity) {
        materialService.inbound(id, quantity);
        return Result.success("入库成功", null);
    }

    /**
     * 出库
     */
    @PutMapping("/outbound/{id}")
    public Result<Void> outbound(@PathVariable Long id, @RequestParam int quantity) {
        materialService.outbound(id, quantity);
        return Result.success("出库成功", null);
    }
}
