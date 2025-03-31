package com.luma.framework.aspect;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.luma.common.annotation.DataScope;
import com.luma.common.domain.SysUserRolePermission;
import com.luma.common.enums.DataScopeEnum;
import com.luma.framework.permission.DataScopeThreadLocal;
import com.luma.framework.permission.IStpInterface;
import com.luma.framework.permission.PermissionThreadLocal;
import com.luma.framework.utils.UserUtil;
import jakarta.annotation.Resource;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据过滤处理
 * @author i-become
 */
@Aspect
@Component
public class DataScopeAspect {

    @Resource
    private IStpInterface stpInterface;

    @Around("@annotation(dataScope)")
    public Object doAround(ProceedingJoinPoint pjp, DataScope dataScope) throws Throwable {
        // 首先清除线程中的数据权限信息
        DataScopeThreadLocal.clean();
        try {
            // 数据权限处理
            handleDataScope(dataScope);
            // 继续执行方法
            return pjp.proceed();
        }finally {
            // 清除线程中的数据权限信息
            DataScopeThreadLocal.clean();
        }
    }

    /**
     * 处理数据权限范围逻辑
     * @param dataScope 数据权限注解
     */
    private void handleDataScope(DataScope dataScope) throws JSQLParserException {
        // 生成权限范围sql
        String sql = sql(dataScope);
        // 将sql转换为mybatis的查询表达式存入线程中，等待后面查询使用
        DataScopeThreadLocal.setSqlSegment(CCJSqlParserUtil.parseCondExpression(sql.substring(4)));
    }

    /**
     * 生成用户权限范围sql
     * @param dataScope 权限范围注解
     * @return
     */
    private String sql(DataScope dataScope){
        // 预处理一下别名
        String deptAlias = dataScope.deptAlias();
        if (StrUtil.isNotBlank(deptAlias)){
            deptAlias = deptAlias + StrUtil.DOT;
        }
        String userAlias = dataScope.userAlias();

        String userIdColumnName = dataScope.userIdColumnName();
        String deptIdColumnName = dataScope.deptIdColumnName();
        Long deptId = UserUtil.getDeptId();
        Long userId = UserUtil.getUserId();

        // 预处理需要的权限，先获取注解中的权限数据，如果没有则获取线程中的数据，线程中的数据来自接口上的权限注解
        String[] permissions;
        if (StringUtils.isNotBlank(dataScope.permission())){
            permissions = dataScope.permission().split(StrUtil.COMMA);
        }else {
            permissions = PermissionThreadLocal.getPermission();
        }

        // 角色关联的数据权限分为以下几种
        // 1.全部权限
        // 2.本部门及部门以下权限
        // 3.仅当前部门权限
        // 4.仅自己权限
        // 5.自定义权限（角色关联的指定n个部门）
        // 获取用户所有关联的有效角色和角色对应的权限范围
        List<SysUserRolePermission> roleList = stpInterface.getRolePermissionList(userId);
        List<String> customRoleIdList = new ArrayList<>();
        DataScopeEnum otherDataScopeMax = DataScopeEnum.NONE;
        for (SysUserRolePermission role: roleList) {
            DataScopeEnum roleDataScope = role.getDataScope();
            List<String> rolePermissionList = role.getPermissionList();
            // 判断接口权限是否满足，如果用户已有权限不包含当前权限要求，那么跳过此角色的判断
            if (permissions != null && rolePermissionList.stream().noneMatch(p -> StrUtil.containsAny(p, permissions))){
                continue;
            }
            // 角色符合接口权限条件，然后根据不同的角色范围做不同的处理
            switch (roleDataScope) {
                // 拥有全部数据权限，那直接返回全部数据权限sql
                case ALL -> {
                    return roleDataScope.getSqlTemplate();
                }
                // 拥有自定义的数据权限，那先记录这个角色编号，用于后面步骤进行sql处理
                case CUSTOM -> customRoleIdList.add(String.valueOf(role.getId()));
                // 其余三种权限范围是依次包含关系，直接取一个最大的就行
                default -> otherDataScopeMax = roleDataScope.getCode() < otherDataScopeMax.getCode() ? roleDataScope : otherDataScopeMax;
            }
        }

        StringBuilder sqlBuilder = new StringBuilder();
        // 如果自定义权限角色编号列表不为空，那说明有自定义的角色信息，要添加自定义的权限数据
        if (!customRoleIdList.isEmpty()){
            sqlBuilder.append(DataScopeEnum.CUSTOM.generateSql(deptAlias, deptIdColumnName, customRoleIdList, deptId, userAlias, userIdColumnName, userId));
            // 自定义权限存在的情况下可以不拼接无权限的sql
            if (otherDataScopeMax == DataScopeEnum.NONE){
                return sqlBuilder.toString();
            }
        }
        // 添加其它sql权限
        sqlBuilder.append(otherDataScopeMax.generateSql(deptAlias, deptIdColumnName, customRoleIdList, deptId, userAlias, userIdColumnName, userId));
        return sqlBuilder.toString();
    }

}
