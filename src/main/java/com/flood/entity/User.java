package com.flood.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户实体
 */
@Data
@TableName("sys_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    @JsonIgnore
    private String password;

    private String realName;

    private String phone;

    private String email;

    private Long roleId;

    /**
     * 状态: 0-禁用, 1-启用
     */
    private Integer status;

    private Date createTime;

    private Date updateTime;

    /**
     * 角色名称（非数据库字段）
     */
    @TableField(exist = false)
    private String roleName;

    /**
     * 角色编码（非数据库字段）
     */
    @TableField(exist = false)
    private String roleCode;
}
