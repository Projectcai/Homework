package com.flood.controller;

import com.flood.common.Result;
import com.flood.dto.LoginDTO;
import com.flood.dto.LoginResultDTO;
import com.flood.dto.RegisterDTO;
import com.flood.entity.Permission;
import com.flood.entity.User;
import com.flood.mapper.UserMapper;
import com.flood.service.UserService;
import com.flood.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Resource
    private UserService userService;

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private UserMapper userMapper;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResultDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        LoginResultDTO result = userService.login(loginDTO);
        return Result.success("登录成功", result);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO registerDTO) {
        userService.register(registerDTO);
        return Result.success("注册成功，请登录", null);
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success("退出成功", null);
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/info")
    public Result<Map<String, Object>> info(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            return Result.unauthorized();
        }
        String username = jwtUtil.getUsernameFromToken(token);
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }
        // 查询用户权限
        List<Permission> permissions = userMapper.selectPermissionsByUserId(user.getId());

        Map<String, Object> info = new HashMap<>();
        info.put("id", user.getId());
        info.put("username", user.getUsername());
        info.put("realName", user.getRealName());
        info.put("phone", user.getPhone());
        info.put("email", user.getEmail());
        info.put("roleId", user.getRoleId());
        info.put("roleName", user.getRoleName());
        info.put("roleCode", user.getRoleCode());
        info.put("permissions", permissions);
        return Result.success(info);
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<Void> changePassword(@RequestBody Map<String, String> body, HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            return Result.unauthorized();
        }
        Long userId = jwtUtil.getUserIdFromToken(token);
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");
        if (oldPassword == null || oldPassword.isEmpty() || newPassword == null || newPassword.isEmpty()) {
            return Result.error("原密码和新密码不能为空");
        }
        userService.changePassword(userId, oldPassword, newPassword);
        return Result.success("密码修改成功", null);
    }

    /**
     * 从请求头中获取Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
