package com.luma.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.luma.common.domain.SysUserAuthRoleInfo;
import com.luma.system.domain.entity.SysRole;
import com.baomidou.mybatisplus.extension.service.IService;
import com.luma.system.domain.vo.SysRoleAddReq;
import com.luma.system.domain.vo.SysRoleBaseListResp;
import com.luma.system.domain.vo.SysRolePageReq;
import com.luma.system.domain.vo.SysRolePageResp;
import com.luma.system.enums.SysStatusEnum;

import java.util.List;

/**
* @author 刘靖
* @description 针对表【sys_role(角色信息表)】的数据库操作Service
* @createDate 2024-08-01 15:51:52
*/
public interface SysRoleService extends IService<SysRole> {

    /**
     * 获取角色菜单权限标识列表
     * @param roleId
     * @return
     */
    List<String> getRoleMenuPermsList(Long roleId);

    /**
     * 获取用户的角色权限列表
     * @param userId 用户id
     * @return
     */
    List<SysUserAuthRoleInfo> getUserRoleAuthList(Long userId);

    /**
     * 获取角色分页
     * @param req
     * @return
     */
    IPage<SysRolePageResp> getRolePage(SysRolePageReq req);

    /**
     * 获取角色列表
     * @return
     */
    List<SysRoleBaseListResp> getRoleList();

    /**
     * 获取角色列表
     * @param userId 用户编号
     * @return
     */
    List<SysRoleBaseListResp> getRoleListByUserId(Long userId);

    /**
     * 添加角色
     * @param req
     */
    void add(SysRoleAddReq req);

    /**
     * 编辑角色
     * @param id 角色编号
     * @param req
     */
    void edit(Long id, SysRoleAddReq req);

    /**
     * 删除角色
     * @param id 角色编号
     */
    void remove(Long id);

    void updateStatus(Long id, SysStatusEnum status);
}
