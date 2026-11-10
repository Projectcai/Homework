package com.flood.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flood.entity.Permission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 权限Mapper
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {
}
