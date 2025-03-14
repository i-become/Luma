package com.luma.framework.interceptor;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.luma.framework.permission.DataScopeThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;

import java.sql.Connection;


/**
 * mybatis拦截器，对权限范围进行处理
 * @author 刘靖
 * @deprecated 已弃用，改成使用 {@link DataScopeInnerInterceptor}，主要由于这种方式与mybatis-plus的分页插件顺序关系不对，导致limit拼接在权限sql之前
 */
//@Component
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
@Slf4j
public class SqlInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        String dataScopeSql = DataScopeThreadLocal.getSql();
        if (StrUtil.isBlank(dataScopeSql)){
            return invocation.proceed();
        }

        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        BoundSql boundSql = statementHandler.getBoundSql();

        // 替换权限范围关键字变量
        String sql = boundSql.getSql();
        if (sql.contains("@isDataScope")){
            sql = sql.replace("@isDataScope", dataScopeSql);
        }else if (DataScopeThreadLocal.getDataScope().autoSql()){
            // 自动拼接
            sql = sql + dataScopeSql;
        }

        // 将修改后的参数对象设置回BoundSql
        ReflectUtil.setFieldValue(boundSql, "sql" ,sql);

        // 继续执行
        return invocation.proceed();
    }

}
