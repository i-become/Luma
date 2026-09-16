package com.luma.system.domain.vo;

import com.luma.common.domain.BasePage;
import com.luma.system.enums.SysStatusEnum;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author i-become
 */
@Data
public class SysTenantPageReq extends BasePage {

    /**
     * 租户名称
     */
    private String name;

    /**
     * 租户别名
     */
    private String alias;

    /**
     * 租户状态（0正常 1停用）,不传表示所有
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

}
