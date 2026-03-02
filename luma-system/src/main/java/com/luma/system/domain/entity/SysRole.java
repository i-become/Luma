package com.luma.system.domain.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;

import com.luma.common.domain.TenantBaseEntity;
import com.luma.common.enums.DataScopeEnum;
import com.luma.system.enums.SysStatusEnum;
import lombok.Data;

/**
 * 角色信息表
 * @TableName sys_role
 */
@TableName(value ="sys_role")
@Data
public class SysRole extends TenantBaseEntity implements Serializable {
    /**
     * 角色ID
     */
    @TableId(type = IdType.AUTO)
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
     * 显示顺序
     */
    private Integer sort;

    /**
     * 数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）
     */
    private DataScopeEnum dataScope;

    /**
     * 角色状态
     */
    private SysStatusEnum status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 删除标志
     */
    @TableLogic
    private Boolean delFlag;

}
