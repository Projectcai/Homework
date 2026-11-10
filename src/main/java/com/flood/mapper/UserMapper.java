package com.flood.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flood.entity.Permission;
import com.flood.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户
     */
    User selectByUsername(String username);

    /**
     * 根据用户ID查询权限列表
     */
    List<Permission> selectPermissionsByUserId(Long userId);
}
