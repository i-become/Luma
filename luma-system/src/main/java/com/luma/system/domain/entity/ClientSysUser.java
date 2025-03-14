package com.luma.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 应用用户关联表
 */
@Data
@TableName(value = "client_sys_user")
public class ClientSysUser {

    /**
     * 应用编号
     */
    private Long clientId;

    /**
     * 用户编号
     */
    private Long userId;

}
