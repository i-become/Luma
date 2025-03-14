package com.luma.system.domain.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author 刘靖
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
    @NotNull(message = "账号不能为空")
    private String loginName;

    /**
     * 密码
     */
    @NotNull(message = "密码不能为空")
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
