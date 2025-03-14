package com.luma.system.mapper;

import com.luma.common.domain.SysUserAuthRoleInfo;
import com.luma.system.domain.entity.SysUserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author 刘靖
* @description 针对表【sys_user_role(用户和角色关联表)】的数据库操作Mapper
* @createDate 2024-08-01 15:51:52
* @Entity com.luma.system.domain.entity.SysUserRole
*/
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    /**
     * 获取用户的角色权限列表
     * @param userId 用户id
     * @return
     */
    List<SysUserAuthRoleInfo> selectUserRoleAuthList(Long userId);

}




