package com.luma.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 系统配置
 * @author i-become
 */
@Data
@Component
@ConfigurationProperties(prefix = "luma.system")
public class SystemConfig {

    /**
     * 超级管理员角色ID
     */
    private Long adminRoleId = 1L;

    /**
     * 超级管理员用户ID
     */
    private Long adminUserId = 1L;

    /**
     * 系统租户ID
     */
    private Long systemTenantId = 1L;

    /**
     * 是否启用多租户
     */
    private Boolean multiTenantEnabled = true;

    /**
     * 是否启用数据权限
     */
    private Boolean dataScopeEnabled = true;

}
