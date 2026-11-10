package com.flood.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flood.entity.Notification;

/**
 * 站内通知服务 (K)
 */
public interface NotificationService {
    Page<Notification> getPage(int pageNum, int pageSize, Long userId, Integer status);
    Notification getById(Long id);
    boolean add(Notification notification);
    boolean markRead(Long id);
    int markAllRead(Long userId);
    int countUnread(Long userId);
}
