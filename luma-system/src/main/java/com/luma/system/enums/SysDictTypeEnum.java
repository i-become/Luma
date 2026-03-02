package com.luma.system.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 字典类型枚举
 * @author i-become
 */
@Getter
public enum SysDictTypeEnum {

    /**
     * 菜单
     */
    MENU("MENU", "菜单"),

    /**
     * 数值
     */
    NUMBER("NUMBER", "数值"),

    /**
     * 字符串
     */
    STRING("STRING", "字符串"),

    /**
     * 数组
     */
    ARRAY("ARRAY", "数组");

    @EnumValue
    @JsonValue
    private final String code;
    
    private final String description;

    SysDictTypeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

}
