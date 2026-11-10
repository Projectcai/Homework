package com.flood.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 调度计划实体
 */
@Data
@TableName("biz_dispatch_plan")
public class DispatchPlan implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String planName;

    private String disasterLocation;

    /**
     * 灾害等级: 1-一般, 2-较大, 3-重大, 4-特大
     */
    private Integer disasterLevel;

    private Long materialId;

    private Integer materialQuantity;

    private Long teamId;

    private Date dispatchTime;

    /**
     * 状态: 0-待执行, 1-执行中, 2-已完成, 3-已取消
     */
    private Integer status;

    private String remark;

    private Date createTime;

    private Date updateTime;

    /**
     * 物资名称（非数据库字段）
     */
    @TableField(exist = false)
    private String materialName;

    /**
     * 队伍名称（非数据库字段）
     */
    @TableField(exist = false)
    private String teamName;
}
