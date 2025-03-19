package com.luma.system.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 字典类型
 * @author i-become
 */
@Getter
@AllArgsConstructor
public enum SysDictTypeEnum {

    /**
     * 菜单
     */
    MENU,

    /**
     * 数值
     */
    NUMBER,

    /**
     * 字符串
     */
    STRING,

    /**
     * 数组
     */
    ARRAY

}
