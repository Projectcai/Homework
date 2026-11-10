package com.flood.dto;

import lombok.Data;

/**
 * 登录响应DTO
 */
@Data
public class LoginResultDTO {

    private String token;
    private Long id;
    private String username;
    private String realName;
    private String roleName;
    private String roleCode;
}
