package com.luma.system.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 字典类型枚举
 * @author i-become
 */
@Getter
@AllArgsConstructor
public enum SysDictTypeEnum {

    /**
     * 菜单
     */
    MENU(0, "菜单"),

    /**
     * 数值
     */
    NUMBER(1, "数值"),

    /**
     * 字符串
     */
    STRING(2, "字符串"),

    /**
     * 数组
     */
    ARRAY(3, "数组");

    @EnumValue
    @JsonValue
    private final int code;
    
    private final String description;

}
