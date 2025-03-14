package com.luma.system.domain.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @author 刘靖
 */
@Data
@Builder
public class SysUserLoginResp {

    /**
     * token
     */
    private String token;

    /**
     * token过期时间
     */
    private Long expiresIn;

    /**
     * 刷新token
     */
    private String refreshToken;

    /**
     * 刷新token过期时间
     */
    private Long refreshExpiresIn;

    /**
     * 用户信息
     */
    private SysLoginUserInfoResp userInfo;

}
