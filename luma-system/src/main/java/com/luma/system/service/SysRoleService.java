package com.luma.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.luma.common.domain.SysRoleDataScopeInfo;
import com.luma.common.domain.SysUserRolePermission;
import com.luma.system.domain.entity.SysRole;
import com.baomidou.mybatisplus.extension.service.IService;
import com.luma.system.domain.vo.SysRoleAddReq;
import com.luma.system.domain.vo.SysRoleBaseListResp;
import com.luma.system.domain.vo.SysRolePageReq;
import com.luma.system.domain.vo.SysRolePageResp;
import com.luma.system.enums.SysStatusEnum;

import java.util.List;

/**
* @author i-become
* 针对表【sys_role(角色信息表)】的数据库操作Service
*/
public interface SysRoleService extends IService<SysRole> {

    /**
     * 获取角色菜单权限标识列表
     * @param id 角色编号
     * @return 角色菜单权限标识列表
     */
    List<String> getRolePermissionListByRoleId(Long id);

    /**
     * 获取用户的角色数据权限列表
     * @param userId 用户编号
     * @return 角色数据权限列表
     */
    List<SysRoleDataScopeInfo> getRoleDataScopeInfoListByUserId(Long userId);

    /**
     * 获取角色分页
     * @param req 角色信息
     * @return 角色分页
     */
    IPage<SysRolePageResp> page(SysRolePageReq req);

    /**
     * 获取角色列表
     * @param userId 用户编号 不传为查询权限范围内数据
     * @return 角色列表
     */
    List<SysRoleBaseListResp> getRoleList(Long userId);

    /**
     * 添加角色
     * @param req 角色信息
     * @return 角色编号
     */
    Long add(SysRoleAddReq req);

    /**
     * 编辑角色
     * @param id 角色编号
     * @param req 角色信息
     */
    void edit(Long id, SysRoleAddReq req);

    /**
     * 删除角色
     * @param id 角色编号
     */
    void remove(Long id);

    /**
     * 更新角色状态
     * @param id 角色编号
     * @param status 角色状态
     */
    void updateStatus(Long id, SysStatusEnum status);
}
