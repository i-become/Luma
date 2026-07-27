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
    DIRECTORY("M", "目录"),
    
    /**
     * 菜单
     */
    MENU("C", "菜单"),
    
    /**
     * 按钮
     */
    BUTTON("F", "按钮"),
    
    /**
     * 外链
     */
    LINK("L", "外链");
    
    @EnumValue
    @JsonValue
    private final String code;
    
    private final String description;

}
