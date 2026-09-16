package com.luma.system.domain.vo;

import com.luma.system.domain.entity.SysTenant;
import com.luma.system.enums.SysStatusEnum;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author i-become
 */
@AutoMapper(target = SysTenant.class)
@Data
public class SysTenantAddReq {

    /**
     * 租户名称
     */
    @NotBlank(message = "{validation.tenant.name.NotBlank}")
    private String name;

    /**
     * 租户别名
     */
    @NotBlank(message = "{validation.tenant.alias.NotBlank}")
    private String alias;

    /**
     * 租户首页
     */
    private String url;

    /**
     * 租户状态（0正常 1停用）
     */
    @NotNull(message = "{validation.tenant.status.NotNull}")
    private SysStatusEnum status;

}
