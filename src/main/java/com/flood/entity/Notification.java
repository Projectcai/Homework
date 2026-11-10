package com.flood.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 站内通知实体 (K)
 */
@Data
@TableName("biz_notification")
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String title;

    private String content;

    /** 类型: INFO/WARNING/DISPATCH */
    private String type;

    /** 状态: 0-未读 1-已读 */
    private Integer status;

    private Date createTime;
}
