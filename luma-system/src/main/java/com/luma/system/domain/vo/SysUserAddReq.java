package com.luma.system.domain.vo;

import com.luma.system.domain.entity.SysUser;
import com.luma.system.enums.SysStatusEnum;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

/**
 * @author i-become
 */
@Data
@AutoMapper(target = SysUser.class)
public class SysUserAddReq {

    /**
     * 部门ID
     */
    @NotNull(message = "部门编号不能为空")
    private Long deptId;

    /**
     * 登录账号
     */
    @NotNull(message = "登录账号不能为空")
    private String loginName;

    /**
     * 用户昵称
     */
    @NotNull(message = "用户昵称不能为空")
    private String username;

    /**
     * 帐号状态（0正常 1停用） 默认正常
     */
    private SysStatusEnum status;

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
     * 密码
     */
    @NotNull(message = "密码不能为空")
    private String password;

    /**
     * 角色编号列表
     */
    private Set<Long> roleIdList;

    /**
     * 岗位编号列表
     */
    private Set<Long> postIdList;

}
