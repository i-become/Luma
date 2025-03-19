//package com.luma.framework.interceptor;
//
//import cn.dev33.satoken.stp.StpUtil;
//import com.luma.common.domain.R;
//import com.luma.common.utils.JsonUtil;
//import com.luma.framework.permission.TenantThreadLocal;
//import com.luma.framework.utils.OAuth2Util;
//import com.luma.framework.utils.UserUtil;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//
///**
// * 登录拦截器
// * @author i-become
// */
//@Component
//public class LoginInterceptor implements HandlerInterceptor {
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//
//        // 检验当前会话是否已经登录
//        if(!StpUtil.isLogin()){
//            response.setStatus(HttpStatus.UNAUTHORIZED.value());
//            response.getWriter().write(JsonUtil.toJsonString(R.fail().code(HttpStatus.UNAUTHORIZED.value())));
//            return false;
//        }
//        TenantThreadLocal.setTenantId(UserUtil.getTenantId());
//        return HandlerInterceptor.super.preHandle(request, response, handler);
//    }
//}
