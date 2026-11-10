package com.flood.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 调度计划请求DTO
 */
@Data
public class DispatchDTO implements Serializable {

    /** 计划名称 */
    @NotBlank(message = "计划名称不能为空")
    private String planName;

    /** 灾害地点 */
    private String disasterLocation;

    /** 灾害级别: 1-一般 2-较大 3-重大 4-特大 */
    private Integer disasterLevel;

    /** 物资ID */
    private Long materialId;

    /** 物资数量 */
    private Integer materialQuantity;

    /** 队伍ID */
    private Long teamId;

    /** 调度时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dispatchTime;

    /** 备注 */
    private String remark;
}
