package com.luma.system.auth;

import cn.dev33.satoken.model.wrapperInfo.SaDisableWrapperInfo;
import cn.dev33.satoken.util.SaTokenConsts;
import com.luma.common.domain.SysRoleDataScopeInfo;
import com.luma.common.domain.SysUserRolePermission;
import com.luma.common.utils.MapstructUtil;
import com.luma.framework.permission.IStpInterface;
import com.luma.system.service.SysRoleService;
import com.luma.system.service.SysUserService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * satoken对于用户权限信息获取的实现
 * @author i-become
 */
@Component
public class StpInterfaceImpl implements IStpInterface {

    @Lazy
    @Resource
    private SysRoleService sysRoleService;

    @Lazy
    @Resource
    private SysUserService sysUserService;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return getRolePermissionList(Long.valueOf(String.valueOf(loginId))).stream()
                // 提取每个角色对应的菜单权限标识列表
                .map(SysUserRolePermission::getPermissionList)
                // 将每个菜单权限标识列表合成一个流
                .flatMap(List::stream)
                // 去重
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return sysRoleService.getRoleDataScopeInfoListByUserId(Long.valueOf(String.valueOf(loginId))).stream().map(SysRoleDataScopeInfo::getRoleKey).toList();
    }

    @Override
    public SaDisableWrapperInfo isDisabled(Object loginId, String service) {
        return sysUserService.isDisable(Long.valueOf(String.valueOf(loginId))) ?
                SaDisableWrapperInfo.createDisabled(-1, SaTokenConsts.MIN_DISABLE_LEVEL) :
                SaDisableWrapperInfo.createNotDisabled();
    }

    @Override
    public List<SysUserRolePermission> getRolePermissionList(Long userId) {
        List<SysUserRolePermission> list = new ArrayList<>();
        // 获取每个角色的权限信息，这里要一个个获取，因为这里会命中缓存，要保证权限缓归属权限模型
        List<SysRoleDataScopeInfo> roles = sysRoleService.getRoleDataScopeInfoListByUserId(userId);
        for (SysRoleDataScopeInfo role : roles) {
            SysUserRolePermission rolePermission = MapstructUtil.convert(role, SysUserRolePermission.class);
            rolePermission.setPermissionList(sysRoleService.getRolePermissionListByRoleId(role.getId()));
            list.add(rolePermission);
        }
        return list;
    }
}
