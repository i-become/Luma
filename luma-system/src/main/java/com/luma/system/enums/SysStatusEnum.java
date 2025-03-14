package com.luma.system.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 系统通用状态
 * @author 刘靖
 */
@Getter
@AllArgsConstructor
public enum SysStatusEnum {

    /**
     * 状态（NORMAL 正常 DISABLED 停用）
     */
    NORMAL(0),
    DISABLED(1);

    @EnumValue
    private final int status;


}
