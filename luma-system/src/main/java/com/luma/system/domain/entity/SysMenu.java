package com.luma.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import com.luma.common.domain.BaseEntity;
import com.luma.system.enums.MenuStatusEnum;
import com.luma.system.enums.MenuTargetEnum;
import com.luma.system.enums.MenuTypeEnum;
import lombok.Data;

/**
 * 系统菜单实体
 * @TableName sys_menu
 */
@TableName(value ="sys_menu")
@Data
public class SysMenu extends BaseEntity implements Serializable {
    /**
     * 菜单编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 父菜单ID，顶层为0
     */
    private Long parentId;

    /**
     * 路由名称 (对应页面组件name，用作KeepAlive缓存标识)
     */
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
     * 菜单图标
     */
    private String icon;

    /**
     * 显示顺序
     */
    private Integer sort;

    /**
     * 菜单标题 (用作document.title和菜单显示名称)
     */
    private String title;

    /**
     * 打开方式
     */
    private MenuTargetEnum target;

    /**
     * 高亮菜单路径 (当前路由不在菜单中时，指定高亮的菜单路径)
     */
    private String activeMenu;

    /**
     * 菜单类型
     */
    private MenuTypeEnum type;

    /**
     * 路由访问路径
     */
    private String path;
    
    /**
     * 外链URL (当type为LINK时使用)
     */
    private String linkUrl;

    /**
     * 是否在菜单中隐藏
     */
    private Boolean hidden;

    /**
     * 是否全屏显示
     */
    private Boolean fullscreen;

    /**
     * 是否固定在标签页 (如首页)
     */
    private Boolean affix;

    /**
     * 是否缓存路由
     */
    private Boolean keepAlive;

    /**
     * 菜单角标文本 (如"NEW"、"HOT")
     */
    private String badge;
    
    /**
     * 菜单角标类型 (如"success"、"warning"、"danger")
     */
    private String badgeType;

    /**
     * 权限标识 (多个用逗号分隔)
     */
    private String perms;
    
    /**
     * 菜单状态
     */
    private MenuStatusEnum status;
    
    /**
     * 备注
     */
    private String remark;

}
