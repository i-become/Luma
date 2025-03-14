package com.luma.system.domain.vo;

import com.luma.common.domain.BasePage;
import com.luma.system.enums.SysStatusEnum;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author 刘靖
 */
@Data
public class SysRolePageReq extends BasePage {

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色权限字符串
     */
    private String roleKey;

    /**
     * 角色状态（0正常 1停用）,不传表示所有
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
     * 用户编号
     */
    private Long userId;

}
