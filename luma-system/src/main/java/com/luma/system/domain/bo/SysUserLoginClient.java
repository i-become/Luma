package com.luma.system.domain.bo;

import lombok.Data;

/**
 * 用户登录的应用信息
 */
@Data
public class SysUserLoginClient {

    /**
     * 应用地址
     */
    private Long clientId;

    /**
     * 应用注销通知地址
     */
    private String logoutCall;

    /**
     * 应用密钥
     */
    private String secret;

}
