package com.luma.system.domain.vo;

import com.luma.system.enums.SysStatusEnum;
import lombok.Data;

/**
 * @author i-become
 */
@Data
public class SysPostBaseListResp {

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

}
