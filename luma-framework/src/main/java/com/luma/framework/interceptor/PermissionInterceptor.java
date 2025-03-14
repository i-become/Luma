package com.luma.framework.interceptor;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.luma.framework.permission.PermissionThreadLocal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;

/**
 * 拦截接口权限注解，并把权限要求存入线程
 * @author 刘靖
 */
public class PermissionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (handler instanceof HandlerMethod handlerMethod){

            Method method = handlerMethod.getMethod();
            SaCheckPermission saCheckPermission = method.getAnnotation(SaCheckPermission.class);
            if (saCheckPermission != null){
                PermissionThreadLocal.sePermission(saCheckPermission.value());
            }

        }

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
