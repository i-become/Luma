package com.luma.system.domain.vo;

import com.luma.common.enums.DataScopeEnum;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author i-become
 */
@Data
public class SysRoleBaseListResp {

    /**
     * 角色ID
     */
    private Long id;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色权限字符串
     */
    private String roleKey;

    /**
     * 数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）
     */
    private DataScopeEnum dataScope;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
