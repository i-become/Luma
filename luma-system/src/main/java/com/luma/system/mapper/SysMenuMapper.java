package com.luma.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.luma.system.domain.entity.SysMenu;
import com.luma.system.domain.vo.SysMenuBaseListResp;

import java.util.List;

/**
 * @author i-become
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * 获取指定角色的菜单列表
     * @param roleId 角色编号
     * @return 菜单列表
     */
    List<SysMenuBaseListResp> selectBaseListByRoleId(Long roleId);

}
