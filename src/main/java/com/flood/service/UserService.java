package com.flood.service;

import com.flood.common.PageResult;
import com.flood.dto.LoginDTO;
import com.flood.dto.LoginResultDTO;
import com.flood.dto.RegisterDTO;
import com.flood.entity.User;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户登录
     */
    LoginResultDTO login(LoginDTO loginDTO);

    /**
     * 用户注册（默认分配"普通操作员"角色）
     */
    void register(RegisterDTO registerDTO);

    /**
     * 分页查询用户
     */
    PageResult<User> getUserPage(int pageNum, int pageSize, String keyword);

    /**
     * 根据ID查询用户
     */
    User getUserById(Long id);

    /**
     * 添加用户
     */
    boolean addUser(User user);

    /**
     * 更新用户
     */
    boolean updateUser(User user);

    /**
     * 删除用户
     */
    boolean deleteUser(Long id);

    /**
     * 重置密码
     */
    boolean resetPassword(Long id);

    /**
     * 修改密码
     */
    boolean changePassword(Long userId, String oldPassword, String newPassword);
}
