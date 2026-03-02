package com.luma.framework.utils;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.luma.framework.config.SystemConfig;
import org.springframework.stereotype.Component;

/**
 * 用户工具
 */
@Component
public class UserUtil {

    private static SystemConfig systemConfig;

    /**
     * 注入SystemConfig
     */
    public UserUtil(SystemConfig systemConfig) {
        UserUtil.systemConfig = systemConfig;
    }

    /**
     * 获取超级管理员角色ID
     */
    public static Long getAdminRoleId() {
        return systemConfig.getAdminRoleId();
    }

    /**
     * 获取超级管理员用户ID
     */
    public static Long getAdminUserId() {
        return systemConfig.getAdminUserId();
    }

    /**
     * 用于存储当前线程的createBy
     */
    private static final ThreadLocal<String> CREATE_BY_THREAD_LOCAL = new TransmittableThreadLocal<>();

    /**
     * 设置创建人
     * @param createBy 创建人名称
     */
    public static void setCreateBy(String createBy) {
        CREATE_BY_THREAD_LOCAL.set(createBy);
    }

    /**
     * 获取创建人
     * @return
     */
    public static String getCreateBy(){
        return CREATE_BY_THREAD_LOCAL.get();
    }

    /**
     * 清除线程中创建人信息
     */
    public static void cleanCreateBy(){
        CREATE_BY_THREAD_LOCAL.remove();
    }

    /**
     * 获取用户编号
     * @return
     */
    public static Long getUserId() {
        return StpUtil.getLoginIdAsLong();
    }

    /**
     * 获取用户昵称
     * @return
     */
    public static String getNickname(){
        try {
            return String.valueOf(StpUtil.getExtra("nickname"));
        }catch (Exception e){
            return null;
        }
    }

    /**
     * 获取用户部门编号
     * @return
     */
    public static Long getDeptId(){
        try {
            return Long.valueOf(String.valueOf(StpUtil.getExtra("deptId")));
        }catch (Exception e){
            return null;
        }
    }

    /**
     * 获取用户租户编号
     * @return
     */
    public static Long getTenantId(){
        return Long.valueOf(String.valueOf(StpUtil.getExtra("tenantId")));
    }

}
