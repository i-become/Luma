package com.luma.system.domain.vo;

import com.luma.system.domain.entity.Client;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 应用详情
 * @author i-become
 */
@AutoMapper(target = Client.class)
@Data
public class ClientInfoResp {

    /**
     * 应用ID
     */
    private Long id;

    /**
     * 应用名称
     */
    private String name;

    /**
     * 应用密钥
     */
    private String secret;

    /**
     * 应用签约的所有用户权限, 多个用逗号隔开
     */
    private String scope;

    /**
     * 应用签约的所有系统权限, 多个用逗号隔开
     */
    private String sysScope;

    /**
     * 应用允许授权的所有URL, 多个用逗号隔开
     */
    private String allowUrl;

    /**
     * 应用允许的所有 grant_type, 多个用逗号隔开
     */
    private String grantType;

    /**
     * 是否静默授权
     */
    private Boolean isConfirm;

    /**
     * 用户注销登录通知地址
     */
    private String logoutCall;

    /**
     * 数据推送地址
     */
    private String pushUrl;

    /**
     * 已授权的用户编号
     */
    private Set<Long> userIds;

    /**
     * 已授权的租户编号
     */
    private Set<Long> tenantIds;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
