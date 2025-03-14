package com.luma.framework.permission;

/**
 * 多租户条件开关
 */
public class TenantThreadLocal {

    /**
     * 系统默认租户编号
     */
    public static final Long SYS_TENANT_ID = 1L;

    private static final ThreadLocal<Integer> THREAD_LOCAL = new ThreadLocal<>();

    private static final ThreadLocal<Long> ID_THREAD_LOCAL = new ThreadLocal<>();


    public static void enable() {
        THREAD_LOCAL.set(1);
    }

    public static void disable() {
        THREAD_LOCAL.set(0);
    }

    public static void disableAll() {
        THREAD_LOCAL.set(-1);
    }

    public static Integer getState(){
        return THREAD_LOCAL.get();
    }

    public static void clearState() {
        THREAD_LOCAL.remove();
    }

    public static void clear() {
        THREAD_LOCAL.remove();
        ID_THREAD_LOCAL.remove();
    }

    public static void setTenantId(Long tenantId) {
        ID_THREAD_LOCAL.set(tenantId);
    }

    public static Long getTenantId() {
        return ID_THREAD_LOCAL.get();
    }

}
