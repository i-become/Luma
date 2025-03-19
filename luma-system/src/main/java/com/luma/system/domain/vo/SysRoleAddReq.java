package com.luma.system.domain.vo;

import com.luma.common.enums.DataScopeEnum;
import com.luma.system.domain.entity.SysRole;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

/**
 * @author i-become
 */
@AutoMapper(target = SysRole.class)
@Data
public class SysRoleAddReq {

    /**
     * 角色名称
     */
    @NotNull(message = "角色名称不能为空")
    private String roleName;

    /**
     * 角色权限字符串
     */
    @NotNull(message = "角色名称不能为空")
    private String roleKey;

    /**
     * 显示顺序
     */
    @NotNull(message = "角色名称不能为空")
    private Integer sort;

    /**
     * 数据范围（1：全部数据权限 2：自定义数据权限 3：本部门及以下数据权限 4：本部门数据权限 5：仅本人数据权限）
     */
    @NotNull(message = "数据权限不能为空")
    private DataScopeEnum dataScope;

    /**
     * 菜单编号列表
     */
    private Set<Long> menuIdList;

    /**
     * 部门编号列表（自定义数据权限需要）
     */
    private Set<Long> deptIdList;

}
