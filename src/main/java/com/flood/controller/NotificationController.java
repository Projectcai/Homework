package com.flood.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flood.common.PageResult;
import com.flood.common.Result;
import com.flood.entity.Notification;
import com.flood.security.LoginUser;
import com.flood.service.NotificationService;
import jakarta.annotation.Resource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 站内通知控制器 (K)
 */
@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    @Resource
    private NotificationService notificationService;

    @GetMapping("/page")
    public Result<PageResult<Notification>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer status,
            @AuthenticationPrincipal LoginUser loginUser) {
        Long userId = loginUser != null ? loginUser.getUser().getId() : null;
        Page<Notification> page = notificationService.getPage(pageNum, pageSize, userId, status);
        PageResult<Notification> pr = new PageResult<>(page.getTotal(), page.getRecords(), pageNum, pageSize);
        return Result.success(pr);
    }

    @GetMapping("/unread-count")
    public Result<Integer> unreadCount(@AuthenticationPrincipal LoginUser loginUser) {
        Long userId = loginUser != null ? loginUser.getUser().getId() : null;
        return Result.success(notificationService.countUnread(userId));
    }

    @PutMapping("/read/{id}")
    public Result<Void> markRead(@PathVariable Long id) {
        notificationService.markRead(id);
        return Result.success("已标记已读", null);
    }

    @PutMapping("/read-all")
    public Result<Integer> markAllRead(@AuthenticationPrincipal LoginUser loginUser) {
        Long userId = loginUser != null ? loginUser.getUser().getId() : null;
        int n = notificationService.markAllRead(userId);
        return Result.success(n + " 条已标记已读", n);
    }
}
