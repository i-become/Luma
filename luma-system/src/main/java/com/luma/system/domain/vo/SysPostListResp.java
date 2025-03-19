package com.luma.system.domain.vo;

import lombok.Data;

/**
 * @author i-become
 */
@Data
public class SysPostListResp {

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

}
