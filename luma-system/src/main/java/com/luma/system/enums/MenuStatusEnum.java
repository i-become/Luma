package com.luma.system.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 菜单状态枚举
 * @author i-become
 */
@Getter
@AllArgsConstructor
public enum MenuStatusEnum {
    
    /**
     * 正常
     */
    NORMAL("0", "正常"),
    
    /**
     * 禁用
     */
    DISABLED("1", "禁用");
    
    @EnumValue
    @JsonValue
    private final String code;
    
    private final String description;
    
}
