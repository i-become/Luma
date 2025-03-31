package com.luma.framework.permission;

import cn.dev33.satoken.stp.StpInterface;
import com.luma.common.domain.SysUserRolePermission;

import java.util.List;

/**
 * @author i-become
 */
public interface IStpInterface extends StpInterface {

    /**
     * 获取用户的角色列表（包含权限范围接口权限等数据）
     * @param userId 用户编号
     * @return
     */
    List<SysUserRolePermission> getRolePermissionList(Long userId);

}
