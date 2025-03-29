package com.luma.framework.interceptor;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.luma.framework.permission.DataScopeThreadLocal;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 权限范围拦截器，动态拼接权限范围sql
 * @author i-become
 */
public class DataScopeInnerInterceptor implements InnerInterceptor {

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        String dataScopeSql = DataScopeThreadLocal.getSql();
        if (StrUtil.isBlank(dataScopeSql)){
            return;
        }

        String sql = boundSql.getSql();
        // 是否包含自定义的权限标识，包含就替换为数据权限sql
        if (sql.contains("@dataScopeSql")){
            sql = sql.replace("@dataScopeSql", dataScopeSql);
        }else if (DataScopeThreadLocal.getDataScope().autoSql()){
            // 自动拼接
            sql = sql + dataScopeSql;
        }
        PluginUtils.MPBoundSql mpBoundSql = PluginUtils.mpBoundSql(boundSql);
        mpBoundSql.sql(sql);

    }

    @Override
    public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
        PluginUtils.MPStatementHandler mpSh = PluginUtils.mpStatementHandler(sh);
        MappedStatement ms = mpSh.mappedStatement();
        SqlCommandType sct = ms.getSqlCommandType();

        String dataScopeSql = DataScopeThreadLocal.getSql();
        if (StrUtil.isBlank(dataScopeSql)){
            return;
        }

        if (sct == SqlCommandType.INSERT || sct == SqlCommandType.UPDATE || sct == SqlCommandType.DELETE) {
            PluginUtils.MPBoundSql mpBs = mpSh.mPBoundSql();
            String sql = mpBs.sql();
            if (sql.contains("@dataScopeSql")){
                sql = sql.replace("@dataScopeSql", dataScopeSql);
            }else if (DataScopeThreadLocal.getDataScope().autoSql()){
                // 自动拼接
                sql = sql + dataScopeSql;
            }
            mpBs.sql(sql);
        }
    }

}
