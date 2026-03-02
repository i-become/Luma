package com.luma.system.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 用户性别枚举
 * @author i-become
 */
@Getter
public enum SysUserSexEnum {

    /**
     * 女
     */
    FEMALE(0, "女"),

    /**
     * 男
     */
    MALE(1, "男"),

    /**
     * 未知
     */
    UNKNOWN(2, "未知");

    @EnumValue
    @JsonValue
    private final Integer code;
    
    private final String description;

    SysUserSexEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

}
