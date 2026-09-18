package com.luma.system.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 应用分页行
 * @author i-become
 */
@Data
public class ClientPageResp {

    /**
     * 应用ID
     */
    private Long id;

    /**
     * 应用名称
     */
    private String name;

    /**
     * 是否静默授权
     */
    private Boolean isConfirm;

    /**
     * 用户注销登录通知地址
     */
    private String logoutCall;

    /**
     * 数据推送地址
     */
    private String pushUrl;

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
