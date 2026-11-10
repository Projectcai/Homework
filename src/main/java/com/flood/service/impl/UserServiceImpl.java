package com.flood.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flood.common.PageResult;
import com.flood.dto.LoginDTO;
import com.flood.dto.LoginResultDTO;
import com.flood.dto.RegisterDTO;
import com.flood.entity.User;
import com.flood.mapper.UserMapper;
import com.flood.service.UserService;
import com.flood.security.JwtTokenProvider;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.Date;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private JwtTokenProvider jwtTokenProvider;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /** H: 用户信息变更后清除登录缓存 */
    private void evictUserCache(String username) {
        if (username == null) return;
        try {
            redisTemplate.delete("user:login:" + username);
        } catch (Exception e) {
            // 缓存清理失败不影响主流程
        }
    }

    @Override
    public LoginResultDTO login(LoginDTO loginDTO) {
        User user = userMapper.selectByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用，请联系管理员");
        }
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername(), user.getRealName());
        LoginResultDTO result = new LoginResultDTO();
        result.setToken(token);
        result.setId(user.getId());
        result.setUsername(user.getUsername());
        result.setRealName(user.getRealName());
        result.setRoleName(user.getRoleName());
        result.setRoleCode(user.getRoleCode());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterDTO registerDTO) {
        // 校验两次密码是否一致
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new RuntimeException("两次输入的密码不一致");
        }
        // 校验用户名是否已存在
        User existing = userMapper.selectByUsername(registerDTO.getUsername());
        if (existing != null) {
            throw new RuntimeException("用户名已存在，请更换");
        }
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setRealName(registerDTO.getRealName());
        user.setPhone(registerDTO.getPhone());
        user.setEmail(registerDTO.getEmail());
        // 默认分配"普通操作员"角色(id=3)
        user.setRoleId(3L);
        user.setStatus(1);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        userMapper.insert(user);
    }

    @Override
    public PageResult<User> getUserPage(int pageNum, int pageSize, String keyword) {
        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(User::getUsername, keyword)
                    .or().like(User::getRealName, keyword)
                    .or().like(User::getPhone, keyword);
        }
        wrapper.orderByDesc(User::getCreateTime);
        userMapper.selectPage(page, wrapper);
        return new PageResult<>(page.getTotal(), page.getRecords(), pageNum, pageSize);
    }

    @Override
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addUser(User user) {
        // 检查用户名是否已存在
        User existing = userMapper.selectByUsername(user.getUsername());
        if (existing != null) {
            throw new RuntimeException("用户名已存在");
        }
        // 密码加密（默认密码123456）
        String defaultPassword = "123456";
        user.setPassword(passwordEncoder.encode(defaultPassword));
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        return userMapper.insert(user) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUser(User user) {
        User old = userMapper.selectById(user.getId());
        String oldUsername = old != null ? old.getUsername() : user.getUsername();
        // 不更新密码字段
        user.setPassword(null);
        user.setUpdateTime(new Date());
        boolean ok = userMapper.updateById(user) > 0;
        evictUserCache(oldUsername);
        return ok;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUser(Long id) {
        User old = userMapper.selectById(id);
        boolean ok = userMapper.deleteById(id) > 0;
        if (old != null) evictUserCache(old.getUsername());
        return ok;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resetPassword(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setPassword(passwordEncoder.encode("123456"));
        user.setUpdateTime(new Date());
        boolean ok = userMapper.updateById(user) > 0;
        evictUserCache(user.getUsername());
        return ok;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateTime(new Date());
        boolean ok = userMapper.updateById(user) > 0;
        evictUserCache(user.getUsername());
        return ok;
    }
}
