package com.luma.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.luma.common.domain.BaseEntity;
import com.luma.system.enums.SysStatusEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * 租户表
 * @TableName sys_dept
 */
@TableName(value ="sys_tenant")
@Data
public class SysTenant extends BaseEntity implements Serializable {

    /**
     * 租户id
     */
    @TableId(type = IdType.AUTO)
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
     * 租户状态
     */
    private SysStatusEnum status;

    /**
     * 删除标志
     */
    @TableLogic
    private Boolean delFlag;

}
