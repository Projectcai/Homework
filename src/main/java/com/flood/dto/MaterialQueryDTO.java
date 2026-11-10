package com.flood.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 物资查询条件DTO
 */
@Data
public class MaterialQueryDTO implements Serializable {

    /** 物资名称(模糊查询) */
    private String materialName;

    /** 物资类型 */
    private String materialType;

    /** 状态: 0-报废 1-正常 2-预警 */
    private Integer status;

    /** 当前页码(默认1) */
    private Integer pageNum = 1;

    /** 每页条数(默认10) */
    private Integer pageSize = 10;
}
