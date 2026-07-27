package com.luma.system.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户类型枚举
 * @author i-become
 */
@Getter
@AllArgsConstructor
public enum UserTypeEnum {
    
    /**
     * 系统用户
     */
    SYSTEM(0, "系统用户"),
    
    /**
     * 注册用户
     */
    REGISTERED(1, "注册用户");
    
    @EnumValue
    @JsonValue
    private final Integer code;
    
    private final String description;

}
