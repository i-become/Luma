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

    /**
     * 获取用户菜单列表
     * @param userId 用户编号
     * @return 菜单列表
     */
    List<SysMenuListResp> list(Long userId);

    /**
     * 获取指定角色的菜单列表
     * @param roleId 角色编号
     * @return 菜单列表
     */
    List<SysMenuBaseListResp> getBaseListByRoleId(Long roleId);

    /**
     * 添加菜单
     * @param req 菜单信息
     * @return 菜单编号
     */
    Long add(SysMenuAddReq req);

    /**
     * 修改菜单
     * @param id 菜单编号
     * @param req 菜单信息
     */
    void edit(Long id, SysMenuAddReq req);

    /**
     * 删除菜单
     * @param id 菜单编号
     */
    void remove(Long id);
}
