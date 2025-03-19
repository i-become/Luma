package com.luma.framework.permission;

/**
 * 接口需要的权限线程变量
 * @author i-become
 */
public class PermissionThreadLocal {

    private static final ThreadLocal<String[]> THREAD_LOCAL = new ThreadLocal<>();

    public static void sePermission(String[] permission){
        THREAD_LOCAL.set(permission);
    }

    public static String[] getPermission(){
        return THREAD_LOCAL.get();
    }

    public static void cleanPermission(){
        THREAD_LOCAL.remove();
    }

}
