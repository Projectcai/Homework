package com.flood.controller;

import com.flood.common.PageResult;
import com.flood.common.Result;
import com.flood.entity.User;
import com.flood.service.UserService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 分页查询用户
     */
    @GetMapping("/page")
    public Result<PageResult<User>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        PageResult<User> result = userService.getUserPage(pageNum, pageSize, keyword);
        return Result.success(result);
    }

    /**
     * 根据ID查询用户
     */
    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return Result.success(user);
    }

    /**
     * 添加用户
     */
    @PostMapping
    public Result<Void> add(@RequestBody User user) {
        userService.addUser(user);
        return Result.success("添加成功", null);
    }

    /**
     * 更新用户
     */
    @PutMapping
    public Result<Void> update(@RequestBody User user) {
        userService.updateUser(user);
        return Result.success("更新成功", null);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success("删除成功", null);
    }

    /**
     * 重置密码
     */
    @PutMapping("/reset-password/{id}")
    public Result<Void> resetPassword(@PathVariable Long id) {
        userService.resetPassword(id);
        return Result.success("密码已重置为默认密码123456", null);
    }
}
