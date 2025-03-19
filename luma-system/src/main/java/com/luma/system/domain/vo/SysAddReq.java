package com.luma.system.domain.vo;

import com.luma.system.domain.entity.SysDict;
import com.luma.system.enums.SysDictTypeEnum;
import com.luma.system.enums.SysStatusEnum;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author i-become
 */
@AutoMapper(target = SysDict.class)
@Data
public class SysAddReq {

    /**
     * 父级id，约定顶层为0
     */
    private Integer parentId;

    /**
     * 名称
     */
    @NotNull(message = "字典名称不能为空")
    private String name;

    /**
     * 字典类型
     */
    @NotNull(message = "字典类型不能为空")
    private SysDictTypeEnum type;

    /**
     * 字典键
     */
    @NotNull(message = "字典键不能为空")
    private String key;

    /**
     * 字典值
     */
    private String value;

    /**
     * 状态（0：正常，1：禁用）
     */
    @NotNull(message = "字典状态不能为空")
    private SysStatusEnum status;

    /**
     * 是否为系统内置
     */
    @NotNull(message = "是否系统内置字段不能为空")
    private Boolean sys;

    /**
     * 排序
     */
    @NotNull(message = "排序不能为空")
    private Integer sort;

    /**
     * 备注
     */
    private String remark;

}
