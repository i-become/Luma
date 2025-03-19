package com.luma.system.domain.vo;

import com.luma.system.domain.entity.SysUser;
import com.luma.system.enums.SysStatusEnum;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.util.List;

/**
 * @author i-become
 */
@Data
@AutoMapper(target = SysUser.class)
public class SysUserInfoResp {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 登录账号
     */
    private String loginName;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 用户类型（0系统用户 1注册用户）
     */
    private Integer userType;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 用户性别（0男 1女 2未知）
     */
    private Integer sex;

    /**
     * 头像路径
     */
    private String avatar;

    /**
     * 帐号状态（0正常 1停用）
     */
    private SysStatusEnum status;

    /**
     * 角色列表
     */
    private List<SysRoleBaseListResp> roleList;

    /**
     * 岗位列表
     */
    private List<SysPostListResp> postList;

    /**
     * 权限列表
     */
    private List<String> permList;

    /**
     * 租户编号
     */
    private Long tenantId;

    /**
     * 租户别名
     */
    private String tenantAlias;

}
