package com.luma.framework.permission;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * 多租户上下文
 */
public class TenantContextHolder {

    /**
     * 系统默认租户编号
     */
    public static final Long SYS_TENANT_ID = 1L;

    /**
     * 多租户状态
     * 默认为null，表示才用配置中忽略的租户表条件进行判断
     * 1：启用
     * 0：禁用线程中下一次查询携带租户条件
     * -1：禁用线程中后续所有查询携带租户条件
     */
    private static final ThreadLocal<Integer> THREAD_LOCAL_STATE = new TransmittableThreadLocal<>();

    /**
     * 租户编号
     */
    private static final ThreadLocal<Long> THREAD_LOCAL = new TransmittableThreadLocal<>();

    /**
     * 启用线程中下一次多租户条件拼接
     */
    public static void enableNext() {
        THREAD_LOCAL_STATE.set(1);
    }

    /**
     * 禁用线程中下一次多租户条件拼接
     */
    public static void disableNext() {
        THREAD_LOCAL_STATE.set(0);
    }

    /**
     * 禁用线程中后续所有多租户条件拼接
     */
    public static void disable() {
        THREAD_LOCAL_STATE.set(-1);
    }

    /**
     * 是否禁用下一次多租户条件拼接
     * @return
     */
    public static boolean isDisableNext(){
        Integer state = THREAD_LOCAL_STATE.get();
        return state != null && state == 0;
    }

    /**
     * 是否禁用多租户
     * @return
     */
    public static boolean isDisable(){
        Integer state = THREAD_LOCAL_STATE.get();
        return state != null && state == -1;
    }

    /**
     * 是否启用下一次多租户条件拼接
     * @return
     */
    public static boolean isEnableNext(){
        Integer state = THREAD_LOCAL_STATE.get();
        return state != null && state == 1;
    }

    /**
     * 是否为默认状态
     * @return
     */
    public static boolean isDefaultState(){
        return THREAD_LOCAL_STATE.get() == null;
    }

    /**
     * 清除状态
     */
    public static void clearState() {
        THREAD_LOCAL_STATE.remove();
    }

    /**
     * 清理状态和租户编号
     */
    public static void clear() {
        THREAD_LOCAL_STATE.remove();
        THREAD_LOCAL.remove();
    }

    /**
     * 设置租户编号
     * @param tenantId
     */
    public static void setTenantId(Long tenantId) {
        THREAD_LOCAL.set(tenantId);
    }

    /**
     * 获取租户编号
     * @return
     */
    public static Long getTenantId() {
        return THREAD_LOCAL.get();
    }

}
