package com.luma.system.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 应用与租户关联表
 */
@Data
@TableName(value ="client_sys_tenant")
public class ClientSysTenant {

    /**
     * 租户编号
     */
    private Long tenantId;

    /**
     * 应用编号
     */
    private Long clientId;

    /**
     * 创建者
     */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}
