package com.luma.system.domain.vo;

import com.luma.system.enums.SysStatusEnum;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author i-become
 */
@Data
public class SysTenantPageResp {

    /**
     * 租户ID
     */
    private Long id;

    /**
     * 租户名称
     */
    private String name;

    /**
     * 租户别名
     */
    private String alias;

    /**
     * 租户首页
     */
    private String url;

    /**
     * 租户状态（0正常 1停用）
     */
    private SysStatusEnum status;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
