package com.luma.system.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 系统性别枚举
 * @author i-become
 */
@Getter
@AllArgsConstructor
public enum SysUserSexEnum {

    /**
     * 女
     */
    GIRL(0),

    /**
     * 男
     */
    BOY(1),

    /**
     * 未知
     */
    UNKNOWN(2);

    @EnumValue
    private final int code;

}
