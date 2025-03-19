package com.luma.system.domain.bo;

import com.luma.system.enums.SysDictTypeEnum;
import com.luma.system.enums.SysStatusEnum;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author i-become
 */
@Data
public class SysDictTreeBo {

    /**
     * 字典编号
     */
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
    private String name;

    /**
     * 字典类型
     */
    private SysDictTypeEnum type;

    /**
     * 上级节点key
     */
    private String parentKey;

    /**
     * 字典键
     */
    private String key;

    /**
     * 字典值
     */
    private Object value;

    /**
     * 状态（0：正常，1：禁用）
     */
    private SysStatusEnum status;

    /**
     * 是否为系统内置
     */
    private Boolean sys;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 当前字典所在的层次
     */
    private Integer level;

    /**
     * 备注
     */
    private String remark;

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
