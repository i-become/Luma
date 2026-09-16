package com.luma.system.domain.vo;

import com.luma.system.enums.SysStatusEnum;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author i-become
 */
@Data
public class SysPostPageResp {

    /**
     * 岗位ID
     */
    private Long id;

    /**
     * 岗位编码
     */
    private String code;

    /**
     * 岗位名称
     */
    private String postName;

    /**
     * 显示顺序
     */
    private Integer sort;

    /**
     * 岗位状态（0正常 1停用）
     */
    private SysStatusEnum status;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
