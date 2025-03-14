package com.luma.common.domain;

import com.luma.common.enums.DataScopeEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author 刘靖
 */
@Data
public class SysUserAuthRoleInfo implements Serializable {

    /**
     * 角色编号
     */
    private Long id;

    /**
     * 角色权限标识
     */
    private String roleKey;

    /**
     * 数据权限范围
     */
    private DataScopeEnum dataScope;

    /**
     * 权限标识列表
     */
    private List<String> permissionList;

}
