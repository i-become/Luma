package com.luma.framework.aspect;

import java.util.ArrayList;
import java.util.List;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.luma.common.annotation.DataScope;
import com.luma.common.domain.SysUserAuthInfo;
import com.luma.common.domain.SysUserAuthRoleInfo;
import com.luma.common.enums.DataScopeEnum;
import com.luma.framework.permission.DataScopeThreadLocal;
import com.luma.framework.permission.PermissionThreadLocal;
import com.luma.framework.permission.IStpInterface;
import com.luma.framework.utils.UserUtil;
import jakarta.annotation.Resource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 数据过滤处理
 * 拦截DataScope注解，更具当前用户的角色和权限结合接口上要求的权限注解生成权限范围查询的sql条件，并存入线程中
 * @author ruoyi
 */
@Aspect
@Component
public class DataScopeAspect
{

    @Resource
    private IStpInterface stpInterface;

    @Around("@annotation(controllerDataScope)")
    public Object doBefore(ProceedingJoinPoint pjp, DataScope controllerDataScope) throws Throwable {
        DataScopeThreadLocal.clean();
        handleDataScope(controllerDataScope);

        Object result = pjp.proceed();

        // 清除线程中的sql
        DataScopeThreadLocal.clean();
        return result;
    }

    protected void handleDataScope(DataScope controllerDataScope)
    {
        // 获取当前的用户
        if (StpUtil.getLoginId() != null)
        {

            // 管理员的操作要做单独处理
            if (UserUtil.isAdmin()){
                // 这一步是为了兼容手动拼接权限的sql
                if (!controllerDataScope.autoSql()){
                    DataScopeThreadLocal.setSql(" ( 1 = 1 )");
                }
                return;
            }

            String permission = controllerDataScope.permission();
            String[] permissions;
            if(StrUtil.isNotBlank(permission)){
                permissions = Convert.toStrArray(permission);
            }else {
                permissions = PermissionThreadLocal.getPermission();
            }
            dataScopeFilter(controllerDataScope.deptAlias(),
                    controllerDataScope.userAlias(), permissions, controllerDataScope);
        }
    }

    /**
     * 数据范围过滤
     * @param deptAlias
     * @param userAlias
     * @param permissions
     * @param controllerDataScope
     */
    public void dataScopeFilter(String deptAlias, String userAlias, String[] permissions, DataScope controllerDataScope)
    {

        // 预处理一下别名
        if (StrUtil.isNotBlank(deptAlias)){
            deptAlias = deptAlias + ".";
        }else {
            deptAlias = "";
        }
        if (StrUtil.isNotBlank(userAlias)){
            userAlias = userAlias + ".";
        }else {
            userAlias = "";
        }
        String deptIdColumnName = controllerDataScope.deptIdColumnName();

        StringBuilder sqlString = new StringBuilder();
        List<DataScopeEnum> conditions = new ArrayList<>();
        List<String> scopeCustomIds = new ArrayList<>();
        SysUserAuthInfo user = stpInterface.getUserAuthInfo(StpUtil.getLoginId());
        List<SysUserAuthRoleInfo> roles = user.getRoles();
        roles.forEach(role -> {
            if (DataScopeEnum.CUSTOM.equals(role.getDataScope()) && role.getPermissionList().stream().anyMatch(r -> StrUtil.containsAny(r, permissions)))
            {
                scopeCustomIds.add(String.valueOf(role.getId()));
            }
        });


        for (SysUserAuthRoleInfo role : roles)
        {
            DataScopeEnum dataScope = role.getDataScope();
            if (conditions.contains(dataScope))
            {
                continue;
            }
            if (permissions != null && permissions.length > 0 && role.getPermissionList().stream().filter(StringUtils::isNotBlank).noneMatch(r -> StrUtil.containsAny(r, permissions)))
            {
                continue;
            }
            if (DataScopeEnum.ALL.equals(dataScope))
            {
                sqlString = new StringBuilder();
                sqlString.append("AND 0 = 0");
                conditions.add(dataScope);
                break;
            }
            else if (DataScopeEnum.CUSTOM.equals(dataScope))
            {
                if (scopeCustomIds.size() > 1)
                {
                    // 多个自定数据权限使用in查询，避免多次拼接。
                    sqlString.append(StrUtil.format(" OR {}{} IN ( SELECT dept_id FROM sys_role_dept WHERE role_id in ({}) ) ", deptAlias, deptIdColumnName, String.join(",", scopeCustomIds)));
                }
                else
                {
                    sqlString.append(StrUtil.format(" OR {}{} IN ( SELECT dept_id FROM sys_role_dept WHERE role_id = {} ) ", deptAlias, deptIdColumnName, role.getId()));
                }
            }
            else if (DataScopeEnum.DEPT.equals(dataScope))
            {
                sqlString.append(StrUtil.format(" OR {}{} = {} ", deptAlias, deptIdColumnName, user.getDeptId()));
            }
            else if (DataScopeEnum.DEPT_AND_CHILD.equals(dataScope))
            {
                sqlString.append(StrUtil.format(" OR {}{} IN ( SELECT id FROM sys_dept WHERE id = {} or find_in_set( {} , ancestors ) )", deptAlias, deptIdColumnName, user.getDeptId(), user.getDeptId()));
            }
            else if (DataScopeEnum.SELF.equals(dataScope))
            {
                if (StrUtil.isNotBlank(userAlias))
                {
                    sqlString.append(StrUtil.format(" OR {}{} = {} ", userAlias, controllerDataScope.userIdColumnName(), user.getId()));
                }
                else
                {
                    // 数据权限为仅本人且没有userAlias别名不查询任何数据
                    sqlString.append(StrUtil.format(" OR {}{} = 0 ", deptAlias, deptIdColumnName));
                }
            }
            conditions.add(dataScope);
        }

        // 角色都不包含传递过来的权限字符，这个时候sqlString也会为空，所以要限制一下,不查询任何数据
        if (conditions.isEmpty())
        {
            sqlString.append(StrUtil.format(" OR {}{} = 0 ", deptAlias, deptIdColumnName));
        }

        if (StrUtil.isNotBlank(sqlString.toString()))
        {
            DataScopeThreadLocal.setDataScope(controllerDataScope);
            // 自动拼接和手动拼接的区别就是前面有没有and符号，因为手动拼接需要在sql中添加@isDataScope占位符，需要在占位符前面使用and符号，比如and @isDataScope,这样才不会报错
            if (controllerDataScope.autoSql()){
                DataScopeThreadLocal.setSql(" AND (" + sqlString.substring(4) + ")");
            }else {
                DataScopeThreadLocal.setSql(" (" + sqlString.substring(4) + ")");
            }
        }
    }

}

