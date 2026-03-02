package com.luma.system.domain.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;

import com.luma.common.domain.TenantBaseEntity;
import com.luma.system.enums.SysDictTypeEnum;
import com.luma.system.enums.SysStatusEnum;
import lombok.Data;

/**
 * 系统字典实体类
 * @TableName sys_dict
 */
@TableName(value ="sys_dict")
@Data
public class SysDict extends TenantBaseEntity implements Serializable {

    /**
     * 字典编号
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 父级id，约定顶层为0
     */
    private Integer parentId;

    /**
     * 祖籍列表
     */
    private String ancestors;

    /**
     * 名称
     */
    @TableField(value = "`name`")
    private String name;

    /**
     * 字典类型
     */
    @TableField(value = "`type`")
    private SysDictTypeEnum type;

    /**
     * 上级节点key
     */
    private String parentKey;

    /**
     * 字典键
     */
    @TableField(value = "`key`")
    private String key;

    /**
     * 字典值
     */
    private String value;

    /**
     * 状态
     */
    private SysStatusEnum status;

    /**
     * 是否为系统内置
     */
    @TableField(value = "`sys`")
    private Boolean sys;

    /**
     * 排序
     */
    @TableField(value = "`sort`")
    private Integer sort;

    /**
     * 当前字典所在的层次
     */
    @TableField(value = "`level`")
    private Integer level;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否删除
     */
    @TableLogic
    private Boolean delFlag;

}