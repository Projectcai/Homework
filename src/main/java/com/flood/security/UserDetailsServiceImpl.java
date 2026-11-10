package com.flood.security;

import com.flood.entity.User;
import com.flood.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Spring Security 用户认证服务实现
 * H: 加入 Redis 缓存, 减少每次请求都查数据库
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final String USER_CACHE_PREFIX = "user:login:";
    private static final long CACHE_EXPIRE_MINUTES = 30;

    private final UserMapper userMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getUserFromCache(username);
        if (user == null) {
            user = userMapper.selectByUsername(username);
            if (user == null) {
                throw new UsernameNotFoundException("用户不存在: " + username);
            }
            putUserToCache(username, user);
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new UsernameNotFoundException("用户已被禁用: " + username);
        }
        return new LoginUser(user);
    }

    /**
     * 先查 Redis 缓存
     */
    private User getUserFromCache(String username) {
        try {
            Object cached = redisTemplate.opsForValue().get(USER_CACHE_PREFIX + username);
            if (cached instanceof User) {
                return (User) cached;
            }
        } catch (Exception e) {
            // Redis 不可用时降级为直接查库, 不影响登录
            log.warn("读取用户缓存失败,降级查库: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 写入 Redis 缓存
     */
    private void putUserToCache(String username, User user) {
        try {
            redisTemplate.opsForValue().set(USER_CACHE_PREFIX + username, user,
                    CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.warn("写入用户缓存失败: {}", e.getMessage());
        }
    }
}
