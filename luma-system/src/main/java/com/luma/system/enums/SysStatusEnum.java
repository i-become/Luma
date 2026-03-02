package com.luma.system.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 系统通用状态枚举
 * @author i-become
 */
@Getter
public enum SysStatusEnum {

    /**
     * 正常
     */
    NORMAL(0, "正常"),
    
    /**
     * 停用
     */
    DISABLED(1, "停用");

    @EnumValue
    @JsonValue
    private final Integer code;
    
    private final String description;

    SysStatusEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

}
