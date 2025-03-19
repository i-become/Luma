package com.luma.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.luma.system.domain.entity.SysMenu;
import com.luma.system.domain.vo.SysMenuAddReq;
import com.luma.system.domain.vo.SysMenuBaseListResp;
import com.luma.system.domain.vo.SysMenuListResp;

import java.util.List;

/**
 * @author i-become
 */
public interface SysMenuService extends IService<SysMenu> {

//    /**
//     * 获取当前用户自己的菜单树
//     * @return
//     */
//    List<Tree<Long>> trees();

    /**
     * 获取当前用户的菜单列表
     * @param name 菜单名称 模糊搜索
     * @return
     */
    List<SysMenuListResp> list(String name);

    /**
     * 获取指定角色的菜单列表
     * @param roleId 角色编号
     * @return
     */
    List<SysMenuBaseListResp> baseList(Long roleId);

    /**
     * 添加菜单
     * @param req
     */
    void add(SysMenuAddReq req);

    /**
     * 修改菜单
     * @param id 菜单编号
     * @param req
     */
    void edit(Long id, SysMenuAddReq req);
}
