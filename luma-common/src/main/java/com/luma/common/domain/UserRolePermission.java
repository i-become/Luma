package com.luma.common.domain;

import com.luma.common.enums.DataScopeEnum;
import lombok.Data;

import java.util.List;

/**
 * 用户角色权限实体
 * @author i-become
 */
@Data
public class UserRolePermission {

    /**
     * 角色编号
     */
    private Long id;

    /**
     * 角色权限标识
     */
    private String roleKey;

    /**
     * 角色的数据权限范围
     */
    private DataScopeEnum dataScope;

    /**
     * 权限标识列表
     */
    private List<String> permissionList;

}
