package com.luma.common.domain;

import com.luma.common.enums.DataScopeEnum;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

/**
 * 系统角色数据权限范围信息
 * @author 刘靖
 */
@Data
@AutoMapper(target = SysUserRolePermission.class)
public class SysRoleDataScopeInfo {

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

}
