package com.flood.common;

/**
 * 系统常量定义
 */
public final class Constants {

    private Constants() {
        throw new UnsupportedOperationException("常量类不可实例化");
    }

    // ==================== Redis Key 前缀 ====================

    /** 登录用户Token的Redis Key前缀 */
    public static final String REDIS_TOKEN_PREFIX = "flood:token:";

    /** 登录验证码的Redis Key前缀 */
    public static final String REDIS_CAPTCHA_PREFIX = "flood:captcha:";

    /** 用户登录信息的Redis Key前缀 */
    public static final String REDIS_USER_PREFIX = "flood:user:";

    // ==================== 角色编码 ====================

    /** 管理员角色编码 */
    public static final String ROLE_ADMIN = "ADMIN";

    /** 防汛指挥人员角色编码 */
    public static final String ROLE_COMMANDER = "COMMANDER";

    /** 普通操作员角色编码 */
    public static final String ROLE_OPERATOR = "OPERATOR";

    // ==================== 通用常量 ====================

    /** 默认页码 */
    public static final int DEFAULT_PAGE_NUM = 1;

    /** 默认每页条数 */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /** 状态-启用 */
    public static final int STATUS_ENABLE = 1;

    /** 状态-禁用 */
    public static final int STATUS_DISABLE = 0;

    /** 成功状态码 */
    public static final int CODE_SUCCESS = 200;

    /** 失败状态码 */
    public static final int CODE_FAIL = 500;

    /** 未认证状态码 */
    public static final int CODE_UNAUTHORIZED = 401;

    /** 无权限状态码 */
    public static final int CODE_FORBIDDEN = 403;
}
