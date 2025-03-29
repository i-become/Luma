package com.luma.framework.permission;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * 接口需要的权限线程变量
 * @author i-become
 */
public class PermissionThreadLocal {

    private static final ThreadLocal<String[]> THREAD_LOCAL = new TransmittableThreadLocal<>();

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
