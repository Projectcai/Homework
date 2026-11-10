package com.flood.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flood.entity.Notification;
import com.flood.mapper.NotificationMapper;
import com.flood.service.NotificationService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 站内通知服务实现 (K)
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    @Resource
    private NotificationMapper notificationMapper;

    @Override
    public Page<Notification> getPage(int pageNum, int pageSize, Long userId, Integer status) {
        Page<Notification> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        // 用户通知 + 公共通知(userId=null)
        wrapper.and(w -> w.eq(Notification::getUserId, userId).or().isNull(Notification::getUserId));
        if (status != null) {
            wrapper.eq(Notification::getStatus, status);
        }
        wrapper.orderByDesc(Notification::getCreateTime);
        return notificationMapper.selectPage(page, wrapper);
    }

    @Override
    public Notification getById(Long id) {
        return notificationMapper.selectById(id);
    }

    @Override
    @Transactional
    public boolean add(Notification notification) {
        notification.setCreateTime(new Date());
        if (notification.getStatus() == null) {
            notification.setStatus(0);
        }
        return notificationMapper.insert(notification) > 0;
    }

    @Override
    @Transactional
    public boolean markRead(Long id) {
        Notification n = notificationMapper.selectById(id);
        if (n == null) return false;
        n.setStatus(1);
        return notificationMapper.updateById(n) > 0;
    }

    @Override
    @Transactional
    public int markAllRead(Long userId) {
        return notificationMapper.markReadByUserId(userId);
    }

    @Override
    public int countUnread(Long userId) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(Notification::getUserId, userId).or().isNull(Notification::getUserId));
        wrapper.eq(Notification::getStatus, 0);
        Long count = notificationMapper.selectCount(wrapper);
        return count == null ? 0 : count.intValue();
    }
}
