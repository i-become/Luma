package com.luma.system.mapper;

import com.luma.system.domain.entity.SysRoleMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.luma.system.domain.vo.SysMenuListResp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author i-become
* @description 针对表【sys_role_menu(角色和菜单关联表)】的数据库操作Mapper
* @createDate 2024-08-01 15:51:52
* @Entity com.luma.system.domain.entity.SysRoleMenu
*/
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {

    /**
     * 获取角色菜单权限标识列表
     * @param roleId
     * @return
     */
    List<String> selectRoleMenuPermsList(Long roleId);

    /**
     * 获取某些角色的所有菜单
     * @param roleIds 角色编号列表
     * @param name 模糊搜索菜单名称
     * @return
     */
    List<SysMenuListResp> selectMenuListByRoleIds(@Param("roleIds") List<Long> roleIds, @Param("name") String name);

}




