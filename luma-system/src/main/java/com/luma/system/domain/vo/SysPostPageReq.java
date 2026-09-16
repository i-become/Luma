package com.luma.system.domain.vo;

import com.luma.common.domain.BasePage;
import com.luma.system.enums.SysStatusEnum;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author i-become
 */
@Data
public class SysPostPageReq extends BasePage {

    /**
     * 岗位编码
     */
    private String code;

    /**
     * 岗位名称
     */
    private String postName;

    /**
     * 岗位状态（0正常 1停用）,不传表示所有
     */
    private SysStatusEnum status;

    /**
     * 创建时间 - 开始
     */
    private LocalDateTime startCreateTime;

    /**
     * 创建时间 - 结束
     */
    private LocalDateTime endCreateTime;

}
