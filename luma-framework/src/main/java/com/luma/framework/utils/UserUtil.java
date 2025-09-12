package com.luma.framework.utils;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.ttl.TransmittableThreadLocal;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

/**
 * 用户工具
 */
public class UserUtil {

    /**
     * 超级管理员roleId
     */
    public static final long ADMIN_ROLE_ID = 1L;

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
     * 获取用户名称
     * @return
     */
    public static String getUsername(){
        try {
            return String.valueOf(StpUtil.getExtra("username"));
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
