package com.luma.system.domain.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author i-become
 */
@Data
@Builder
public class SysLoginUserInfoResp {

    /**
     * 用户编号
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 角色列表
     */
    private List<String> role;

}
