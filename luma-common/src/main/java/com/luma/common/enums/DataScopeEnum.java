package com.luma.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据权限枚举
 */
@Getter
@AllArgsConstructor
public enum DataScopeEnum {

    /**
     * 全部数据权限
     */
    ALL(1),

    /**
     * 自定数据权限
     */
    CUSTOM(2),

    /**
     * 部门及以下数据权限
     */
    DEPT_AND_CHILD(3),

    /**
     * 部门数据权限
     */
    DEPT(4),

    /**
     * 仅本人数据权限
     */
    SELF(5);

    @EnumValue
    private final int code;

}
