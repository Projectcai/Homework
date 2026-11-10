package com.flood.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 防汛物资实体
 */
@Data
@TableName("biz_material")
public class Material implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String materialName;

    private String materialType;

    private String unit;

    private Integer quantity;

    /** 库存预警值 */
    private Integer warningValue;

    /** 水文监测预警阈值(用于监测模块,如水位警戒值) (I+J) */
    private Double monitorThreshold;

    private String warehouseLocation;

    private String supplier;

    private String supplierPhone;

    /** 状态: 0-停用, 1-正常 */
    private Integer status;

    private String remark;

    private Date createTime;

    private Date updateTime;
}
