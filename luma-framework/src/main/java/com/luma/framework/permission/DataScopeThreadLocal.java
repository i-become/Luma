package com.luma.framework.permission;

import com.luma.common.annotation.DataScope;

/**
 * 数据权限范围线程变量
 * @author i-become
 */
public class DataScopeThreadLocal {

    private static final ThreadLocal<String> SQL_LOCAL = new ThreadLocal<>();

    private static final ThreadLocal<DataScope> DATA_SCOPE_LOCAL = new ThreadLocal<>();

    public static void setDataScope(DataScope dataScope){
        DATA_SCOPE_LOCAL.set(dataScope);
    }

    public static void setSql(String sql){
        SQL_LOCAL.set(sql);
    }

    public static String getSql(){
        return SQL_LOCAL.get();
    }

    public static DataScope getDataScope(){
        return DATA_SCOPE_LOCAL.get();
    }

    public static void clean(){
        DATA_SCOPE_LOCAL.remove();
        SQL_LOCAL.remove();
    }

}
