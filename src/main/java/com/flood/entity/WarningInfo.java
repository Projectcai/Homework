package com.flood.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 预警信息实体
 */
@Data
@TableName("biz_warning_info")
public class WarningInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    /**
     * 预警类型: flood-洪水, rainstorm-暴雨, typhoon-台风, landslide-滑坡
     */
    private String warningType;

    /**
     * 预警等级: 1-蓝色, 2-黄色, 3-橙色, 4-红色
     */
    private Integer warningLevel;

    private String content;

    private String affectedArea;

    private Date publishTime;

    /**
     * 状态: 0-草稿, 1-已发布, 2-已解除
     */
    private Integer status;

    private Date createTime;

    private Date updateTime;
}
