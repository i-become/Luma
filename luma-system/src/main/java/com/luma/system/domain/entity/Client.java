package com.luma.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.luma.common.domain.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * 应用表
 */
@Data
@TableName(value ="client")
public class Client extends BaseEntity implements Serializable {

    /**
     * 应用id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 应用名称
     */
    private String name;

    /**
     * 应用密钥
     */
    private String secret;

    /**
     * 应用签约的所有用户权限, 多个用逗号隔开
     */
    private String scope;

    /**
     * 应用签约的所有系统权限, 多个用逗号隔开
     */
    private String sysScope;

    /**
     * 应用允许授权的所有URL, 多个用逗号隔开 （可以使用 * 号通配符）
     */
    private String allowUrl;

    /**
     * 应用允许的所有 grant_type(权限处理器), 多个用逗号隔开
     */
    private String grantType;

    /**
     * 是否静默授权（不会弹出授权页，直接授权）
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
     * 删除标志（0代表存在 1代表删除）
     */
    @TableLogic
    private Boolean delFlag;

}
