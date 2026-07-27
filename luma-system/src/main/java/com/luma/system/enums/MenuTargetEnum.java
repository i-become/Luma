package com.luma.system.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 菜单打开方式枚举
 * @author i-become
 */
@Getter
@AllArgsConstructor
public enum MenuTargetEnum {
    
    /**
     * 页签
     */
    TAB("T", "页签"),
    
    /**
     * 新窗口
     */
    NEW_WINDOW("N", "新窗口");
    
    @EnumValue
    @JsonValue
    private final String code;
    
    private final String description;
}
