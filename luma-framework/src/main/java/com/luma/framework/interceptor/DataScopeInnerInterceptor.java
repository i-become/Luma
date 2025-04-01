package com.luma.framework.interceptor;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.luma.framework.permission.DataScopeThreadLocal;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
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
        PluginUtils.MPBoundSql mpBoundSql = PluginUtils.mpBoundSql(boundSql);
        mpBoundSql.sql(getSetDataScopeSqlSegment(boundSql.getSql()));
    }

    @Override
    public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
        PluginUtils.MPStatementHandler mpSh = PluginUtils.mpStatementHandler(sh);
        MappedStatement ms = mpSh.mappedStatement();
        SqlCommandType sct = ms.getSqlCommandType();

        if (sct == SqlCommandType.INSERT || sct == SqlCommandType.UPDATE || sct == SqlCommandType.DELETE) {
            PluginUtils.MPBoundSql mpBs = mpSh.mPBoundSql();
            mpBs.sql(getSetDataScopeSqlSegment(mpBs.sql()));
        }
    }

    /**
     * 获取设置了权限范围数据的sql
     * @param sql 原始sql
     * @return 带权限范围数据的sql
     */
    private String getSetDataScopeSqlSegment(String sql){
        Expression sqlSegment = DataScopeThreadLocal.getSqlSegment();
        if (sqlSegment == null){
            return sql;
        }
        // 将权限条件加入到sql中
        Statement statement;
        try {
            statement = CCJSqlParserUtil.parse(sql);
        } catch (JSQLParserException e) {
            throw new RuntimeException(e);
        }
        if (statement instanceof Select select) {
            PlainSelect plainSelect = select.getPlainSelect();
            plainSelect.setWhere(appendCondition(plainSelect.getWhere(), sqlSegment));
        } else if (statement instanceof Update update) {
            update.setWhere(appendCondition(update.getWhere(), sqlSegment));
        } else if (statement instanceof Delete delete) {
            delete.setWhere(appendCondition(delete.getWhere(), sqlSegment));
        }
        return statement.toString();
    }

    /**
     * 辅助方法：拼接条件
     */
    private Expression appendCondition(Expression original, Expression sqlSegment) {
        return original == null ? sqlSegment : new AndExpression(original, sqlSegment);
    }

}
