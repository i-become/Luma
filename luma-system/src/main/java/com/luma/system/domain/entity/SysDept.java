package com.luma.system.domain.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;

import com.luma.common.domain.TenantBaseEntity;
import com.luma.system.enums.SysStatusEnum;
import lombok.Data;

/**
 * 部门表
 * @TableName sys_dept
 */
@TableName(value ="sys_dept")
@Data
public class SysDept extends TenantBaseEntity implements Serializable {
    /**
     * 部门id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 父部门id
     */
    private Long parentId;

    /**
     * 祖级列表
     */
    private String ancestors;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 显示顺序
     */
    private Integer sort;

    /**
     * 负责人
     */
    private String leader;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 部门状态
     */
    private SysStatusEnum status;

    /**
     * 删除标志
     */
    @TableLogic
    private Boolean delFlag;

}
