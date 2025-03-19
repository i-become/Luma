package com.luma.common.domain;

import lombok.Data;

import java.util.List;

/**
 * @author i-become
 */
@Data
public class SysUserAuthInfo {

    /**
     * 用户编号
     */
    private Long id;

    /**
     * 租户编号
     */
    private Long tenantId;

    /**
     * 部门编号
     */
    private Long deptId;

    /**
     * 角色权限信息列表
     */
    private List<SysUserAuthRoleInfo> roles;

}
