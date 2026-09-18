package com.luma.system.domain.vo;

import lombok.Data;

/**
 * 重置应用密钥响应
 * @author i-become
 */
@Data
public class ClientSecretResp {

    /**
     * 应用ID
     */
    private Long id;

    /**
     * 新的应用密钥
     */
    private String secret;

}
