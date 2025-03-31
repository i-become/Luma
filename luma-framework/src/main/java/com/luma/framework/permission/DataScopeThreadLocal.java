package com.luma.framework.permission;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.luma.common.annotation.DataScope;
import net.sf.jsqlparser.expression.Expression;

import java.util.Map;

/**
 * 数据权限范围线程变量
 * @author i-become
 */
public class DataScopeThreadLocal {

    private static final ThreadLocal<Expression> SQL_LOCAL = new TransmittableThreadLocal<>();

    public static void setSqlSegment(Expression SqlSegment){
        SQL_LOCAL.set(SqlSegment);
    }

    public static Expression getSqlSegment(){
        return SQL_LOCAL.get();
    }

    public static void clean(){
        SQL_LOCAL.remove();
    }

}
