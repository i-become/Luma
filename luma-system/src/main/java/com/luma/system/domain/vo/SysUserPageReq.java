package com.luma.system.domain.vo;

import com.luma.common.domain.BasePage;
import com.luma.system.enums.SysStatusEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysUserPageReq extends BasePage {

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 登录账号
     */
    private String loginName;

    /**
     * 用户名
     */
    private String username;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 帐号状态（0正常 1停用） 不传为全部
     */
    private SysStatusEnum status;

    /**
     * 创建时间 - 开始
     */
    private LocalDateTime startCreateTime;

    /**
     * 创建时间 - 结束
     */
    private LocalDateTime endCreateTime;

    /**
     * 角色编号
     */
    private Long roleId;

}
