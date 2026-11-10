package com.flood.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 应急队伍实体
 */
@Data
@TableName("biz_emergency_team")
public class EmergencyTeam implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String teamName;

    private String teamType;

    private String leaderName;

    private String leaderPhone;

    private Integer memberCount;

    private String location;

    private String equipment;

    /** 道路状况: 良好/一般/较差 (G) */
    private String roadCondition;

    /** 经度 (L) */
    private Double longitude;

    /** 纬度 (L) */
    private Double latitude;

    /** 状态: 0-休整 1-待命 2-出动中 */
    private Integer status;

    private Date createTime;

    private Date updateTime;
}
