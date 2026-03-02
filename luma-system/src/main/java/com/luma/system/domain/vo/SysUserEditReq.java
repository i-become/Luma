package com.luma.system.domain.vo;

import com.luma.system.domain.entity.SysUser;
import com.luma.system.enums.SysStatusEnum;
import com.luma.system.enums.SysUserSexEnum;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

/**
 * @author i-become
 */
@AutoMapper(target = SysUser.class)
@Data
public class SysUserEditReq {

    /**
     * 部门ID
     */
    @NotNull(message = "{validation.dept.id.NotNull}")
    private Long deptId;

    /**
     * 用户昵称
     */
    @NotNull(message = "{validation.user.nickname.NotBlank}")
    private String nickname;

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
     * 用户性别
     */
    private SysUserSexEnum sex;

    /**
     * 头像路径
     */
    private String avatar;

    /**
     * 角色编号列表
     */
    private Set<Long> roleIds;

    /**
     * 岗位编号列表
     */
    private Set<Long> postIds;

}
