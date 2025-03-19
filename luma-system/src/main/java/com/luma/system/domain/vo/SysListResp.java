package com.luma.system.domain.vo;

import com.luma.system.enums.SysDictTypeEnum;
import com.luma.system.enums.SysStatusEnum;
import lombok.Data;

/**
 * @author i-become
 */
@Data
public class SysListResp {

    /**
     * 字典编号
     */
    private Integer id;

    /**
     * 父级id，约定顶层为0
     */
    private Integer parentId;

    /**
     * 名称
     */
    private String name;

    /**
     * 字典类型
     */
    private SysDictTypeEnum type;

    /**
     * 字典键
     */
    private String key;

    /**
     * 字典值
     */
    private String value;

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
     * 备注
     */
    private String remark;

}
