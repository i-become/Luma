package com.luma.system.domain.vo;

import lombok.Data;

/**
 * @author 刘靖
 */
@Data
public class SysMenuBaseListResp {

    /**
     * 菜单编号
     */
    private Long id;

    /**
     * 父菜单ID，约定顶层为0
     */
    private Long parentId;

    /**
     * 显示顺序
     */
    private Integer sort;

    /**
     * 路由标题 (用作 document.title || 菜单的名称)
     */
    private String title;

    /**
     * 是否在菜单中隐藏
     */
    private Boolean isHide;

    /**
     * 权限标识
     */
    private String perms;

    /**
     * 是否选关联该角色
     */
    private Boolean checked;

}
