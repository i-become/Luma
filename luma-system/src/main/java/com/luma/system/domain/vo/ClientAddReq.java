package com.luma.system.domain.vo;

import com.luma.system.domain.entity.Client;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

/**
 * 添加/编辑应用
 * @author i-become
 */
@AutoMapper(target = Client.class)
@Data
public class ClientAddReq {

    /**
     * 应用名称
     */
    @NotBlank(message = "{validation.client.name.NotBlank}")
    private String name;

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
    @NotNull(message = "{validation.client.isConfirm.NotNull}")
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
     * 授权用户编号列表
     */
    private Set<Long> userIds;

    /**
     * 授权租户编号列表
     */
    private Set<Long> tenantIds;

}
