package com.luma.system.domain.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author i-become
 */
@Data
public class SysUserLoginReq {

    /**
     * 租户别名
     */
    private String tenantAlias;

    /**
     * 租户编号
     */
    private Long tenantId;

    /**
     * 账号
     */
    @NotBlank(message = "{validation.auth.loginName.NotBlank}")
    private String loginName;

    /**
     * 密码
     */
    @NotBlank(message = "{validation.auth.password.NotBlank}")
    private String password;

    /**
     * 验证码
     */
//    @NotNull(message = "验证码不能为空")
    private String code;

    /**
     * 是否记住我
     */
    private Boolean rememberMe;

}
