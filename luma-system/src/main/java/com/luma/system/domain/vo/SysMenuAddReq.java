package com.luma.system.domain.vo;

import com.luma.system.domain.entity.SysMenu;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author i-become
 */
@Data
@AutoMapper(target = SysMenu.class)
public class SysMenuAddReq {

    /**
     * 父菜单ID，约定顶层为0
     */
    private Long parentId;

    /**
     * 路由 name (对应页面组件 name, 可用作 KeepAlive 缓存标识 && 按钮权限筛选)
     */
    @NotNull(message = "路由名称不能为空")
    private String name;

    /**
     * 路由重定向地址
     */
    private String redirect;

    /**
     * 视图文件路径
     */
    private String component;

    /**
     * 菜单和面包屑对应的图标
     */
    private String icon;

    /**
     * 显示顺序
     */
    @NotNull(message = "显示顺序不能为空")
    private Integer sort;

    /**
     * 路由标题 (用作 document.title || 菜单的名称)
     */
    private String title;

    /**
     * 打开方式(T页签 N新窗口)
     */
    private String target;

    /**
     * 菜单栏高亮,默认激活菜单的 index
     */
    private String active;

    /**
     * 菜单类型（M目录 C菜单 F按钮 L外链）
     */
    @NotNull(message = "菜单类型不能为空")
    private String type;

    /**
     * 路由访问路径
     */
    private String path;

    /**
     * 是否在菜单中隐藏
     */
    private Boolean isHide;

    /**
     * 菜单是否全屏
     */
    private Boolean isFull;

    /**
     * 菜单是否固定在标签页中 (首页通常是固定项)
     */
    private Boolean isAffix;

    /**
     * 是否缓存路由
     */
    private Boolean isKeepAlive;

    /**
     * 标签，会在菜单栏中显示红色角标
     */
    private String tag;

    /**
     * 权限标识
     */
    private String perms;

}
