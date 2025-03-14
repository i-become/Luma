package com.luma.system.domain.vo;

import com.luma.system.domain.entity.SysDict;
import com.luma.system.enums.SysStatusEnum;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author 刘靖
 */
@AutoMapper(target = SysDict.class)
@Data
public class SysEditReq {

    /**
     * 名称
     */
    @NotNull(message = "字典名称不能为空")
    private String name;

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
