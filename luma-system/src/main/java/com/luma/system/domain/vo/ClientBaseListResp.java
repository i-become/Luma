package com.luma.system.domain.vo;

import lombok.Data;

/**
 * 应用基础列表（下拉等）
 * @author i-become
 */
@Data
public class ClientBaseListResp {

    /**
     * 应用ID
     */
    private Long id;

    /**
     * 应用名称
     */
    private String name;

}
