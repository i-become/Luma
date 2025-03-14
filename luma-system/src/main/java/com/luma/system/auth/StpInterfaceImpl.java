package com.luma.system.auth;

import com.luma.common.domain.SysUserAuthInfo;
import com.luma.common.domain.SysUserAuthRoleInfo;
import com.luma.framework.permission.IStpInterface;
import com.luma.framework.utils.UserUtil;
import com.luma.system.domain.entity.SysRole;
import com.luma.system.service.SysRoleService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义权限加载接口实现类，如有自定义权限需求，参考https://sa-token.cc/doc.html#/use/jur-auth
 * @author 刘靖
 */
@Component
public class StpInterfaceImpl implements IStpInterface {

    @Resource
    private SysRoleService sysRoleService;

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return getSysUserAuthInfoList(Long.valueOf(String.valueOf(loginId))).stream()
                .map(SysUserAuthRoleInfo::getPermissionList)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * 返回一个账号所拥有的角色标识集合
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return getSysUserAuthInfoList(Long.valueOf(String.valueOf(loginId))).stream()
                .map(SysUserAuthRoleInfo::getRoleKey)
                .collect(Collectors.toList());
    }

    @Override
    public SysUserAuthInfo getUserAuthInfo(Object loginId){
        if (loginId == null){
            return new SysUserAuthInfo();
        }
        Long userId = Long.valueOf(String.valueOf(loginId));
        SysUserAuthInfo authInfo = new SysUserAuthInfo();
        authInfo.setId(userId);
        authInfo.setTenantId(UserUtil.getTenantId());
        authInfo.setDeptId(UserUtil.getDeptId());
        authInfo.setRoles(getSysUserAuthInfoList(Long.valueOf(String.valueOf(loginId))));
        return authInfo;
    }

    public List<SysUserAuthRoleInfo> getSysUserAuthInfoList(Long userId){
        if (UserUtil.ADMIN_ID.equals(userId)){
            return sysRoleService.lambdaQuery().select(SysRole::getId, SysRole::getRoleKey, SysRole::getDataScope).list().stream().map(o -> {
                SysUserAuthRoleInfo info = new SysUserAuthRoleInfo();
                info.setId(o.getId());
                info.setRoleKey(o.getRoleKey());
                info.setDataScope(o.getDataScope());
                info.setPermissionList(sysRoleService.getRoleMenuPermsList(o.getId()));
                return info;
            }).toList();
        }
        List<SysUserAuthRoleInfo> list = sysRoleService.getUserRoleAuthList(userId);
        list.forEach(role -> role.setPermissionList(sysRoleService.getRoleMenuPermsList(role.getId())));
        return list;
    }

}
