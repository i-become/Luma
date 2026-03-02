package com.luma.system.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.luma.common.domain.TenantBaseEntity;
import com.luma.system.enums.SysStatusEnum;
import com.luma.system.enums.SysUserSexEnum;
import com.luma.system.enums.UserTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户信息表
 * @author i-become
 * @TableName sys_user
 */
@TableName(value = "sys_user")
@Data
public class SysUser extends TenantBaseEntity implements Serializable {

    /**
     * 用户ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 登录账号
     */
    private String loginName;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户类型
     */
    private UserTypeEnum userType;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 用户性别
     */
    private SysUserSexEnum sex;

    /**
     * 头像路径
     */
    private String avatar;

    /**
     * 密码
     */
    @JsonIgnore
    @TableField(select = false)
    private String password;

    /**
     * 盐加密
     */
    @JsonIgnore
    @TableField(select = false)
    private String salt;

    /**
     * 帐号状态
     */
    private SysStatusEnum status;

    /**
     * 删除标志
     */
    @TableLogic
    private Boolean delFlag;

    /**
     * 最后登录IP
     */
    private String loginIp;

    /**
     * 最后登录时间
     */
    private LocalDateTime loginDate;

    /**
     * 密码最后更新时间
     */
    private LocalDateTime pwdUpdateDate;

}
