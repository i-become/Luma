package com.luma.framework.utils;

import cn.dev33.satoken.stp.StpUtil;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

/**
 * 用户工具
 */
public class UserUtil {

    public static final Long ADMIN_ID = 0L;

    /**
     * 用于存储当前线程的createBy
     */
    private static final ThreadLocal<String> CREATE_BY_THREAD_LOCAL = new ThreadLocal<>();

    public static void setCreateBy(String createBy) {
        CREATE_BY_THREAD_LOCAL.set(createBy);
    }

    public static String getCreateBy(){
        return CREATE_BY_THREAD_LOCAL.get();
    }

    public static void cleanCreateBy(){
        CREATE_BY_THREAD_LOCAL.remove();
    }

    public static Long getUserId() {
        return StpUtil.getLoginIdAsLong();
    }

    public static String getUsername(){
        try {
            return String.valueOf(StpUtil.getExtra("username"));
        }catch (Exception e){
            return null;
        }
    }

    public static Long getDeptId(){
        try {
            return Long.valueOf(String.valueOf(StpUtil.getExtra("deptId")));
        }catch (Exception e){
            return null;
        }
    }

    public static Long getTenantId(){
        try {
            return Long.valueOf(String.valueOf(StpUtil.getExtra("tenantId")));
       }catch (Exception e){
            return null;
        }
    }

//    public static String getCreateBy(){
//        // TODO 这里是为了MyMetaObjectHandler中获取当前创建人信息使用，有优化空间
//        if (!isWebThread()){
//            return null;
//        }
//        try {
//            return String.valueOf(StpUtil.getExtra("username"));
//        }catch (Exception e){
//            try {
//                return String.valueOf(OAuth2Util.getClientId());
//            }catch (Exception e1){
//                return null;
//            }
//        }
//    }

    public static boolean isAdmin(){
        return getUserId().equals(ADMIN_ID);
    }

    public static List<String> getPermissionList(){
        return StpUtil.getPermissionList();
    }

    public static boolean isWebThread() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return attributes instanceof ServletRequestAttributes;
    }

}
