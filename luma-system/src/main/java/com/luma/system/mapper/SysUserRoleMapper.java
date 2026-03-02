package com.luma.system.mapper;

import com.luma.common.domain.SysRoleDataScopeInfo;
import com.luma.system.domain.entity.SysUserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * 针对表【sys_user_role(用户和角色关联表)】的数据库操作Mapper
* @author i-become
*/
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    /**
     * 获取用户的角色数据权限列表
     * @param userId 用户编号
     * @return
     */
    List<SysRoleDataScopeInfo> selectRoleDataScopeInfoListByUserId(Long userId);

    /**
     * 根据角色ID查询用户ID列表
     * @param roleId 角色ID
     * @return 用户ID列表
     */
    List<Long> selectUserIdsByRoleId(Long roleId);

}




