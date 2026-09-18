package com.luma.system.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 菜单类型枚举
 * @author i-become
 */
@Getter
@AllArgsConstructor
public enum MenuTypeEnum {
    
    /**
     * 目录
     */
    DIRECTORY(0, "目录"),
    
    /**
     * 菜单
     */
    MENU(1, "菜单"),
    
    /**
     * 按钮
     */
    BUTTON(2, "按钮"),
    
    /**
     * 外链
     */
    LINK(3, "外链");
    
    @EnumValue
    @JsonValue
    private final int code;
    
    private final String description;

}
