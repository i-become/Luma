package com.luma.openapi.domain.vo;

import com.luma.system.domain.entity.SysTenant;
import com.luma.system.enums.SysStatusEnum;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

/**
 * @author i-become
 */
@Data
@AutoMapper(target = SysTenant.class)
public class SysTenantInfoResp {

    /**
     * 租户id
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
     * 租户状态（0正常 1停用）
     */
    private SysStatusEnum status;

}
