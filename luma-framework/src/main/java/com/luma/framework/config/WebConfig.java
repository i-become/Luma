package com.luma.framework.config;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.annotation.handler.SaAnnotationHandlerInterface;
import cn.dev33.satoken.application.ApplicationInfo;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.template.SaOAuth2Util;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.strategy.SaAnnotationStrategy;
import com.luma.framework.interceptor.JacksonEnumConverterFactory;
import com.luma.framework.interceptor.PermissionInterceptor;
import com.luma.framework.interceptor.ThreadLocalCleanupFilter;
import com.luma.framework.interceptor.UtcToLocalDateTimeDeserializer;
import com.luma.framework.permission.TenantContextHolder;
import com.luma.framework.utils.OAuth2Util;
import com.luma.framework.utils.UserUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * web配置
 * @author i-become
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private WebProperties webProperties;

    @Resource
    private JacksonEnumConverterFactory jacksonEnumConverterFactory;

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // ApplicationInfo.routePrefix 配置是为了SaHolder.getRequest().isPath()正确匹配
        ApplicationInfo.routePrefix = webProperties.getOpenApi().getPrefix();
        configurePathMatch(configurer, webProperties.getWebApi());
        configurePathMatch(configurer, webProperties.getOpenApi());
    }

    private void configurePathMatch(PathMatchConfigurer configurer, WebProperties.Api api){
        AntPathMatcher antPathMatcher = new AntPathMatcher(".");
        configurer.addPathPrefix(api.getPrefix(), clazz -> clazz.isAnnotationPresent(RestController.class)
                && antPathMatcher.match(api.getController(), clazz.getPackage().getName()));
    }

    /**
     * 自定义拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        SaAnnotationStrategy.instance.checkMethodAnnotation = (method) -> {
            StpUtil.checkLogin();
            TenantContextHolder.setTenantId(UserUtil.getTenantId());
            UserUtil.setCreateBy(UserUtil.getNickname());

            // 如果 Method 或其所属 Class 上有 @SaIgnore 注解，则直接跳过整个校验过程
            if(SaAnnotationStrategy.instance.isAnnotationPresent.apply(method, SaIgnore.class)) {
                SaRouter.stop();
            }
            // 先校验 Method 所属 Class 上的注解
            SaAnnotationStrategy.instance.checkElementAnnotation.accept(method.getDeclaringClass());
            // 再校验 Method 上的注解
            SaAnnotationStrategy.instance.checkElementAnnotation.accept(method);
        };
        // 登录拦截
        registry.addInterceptor(new SaInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(webProperties.getWebApi().getExcludePathPatterns())
                .excludePathPatterns(webProperties.getOpenApi().getPrefix() + "/**");

        // 权限拦截
        registry.addInterceptor(new PermissionInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(webProperties.getWebApi().getExcludePathPatterns())
                .excludePathPatterns(webProperties.getOpenApi().getPrefix() + "/**");

        // openapi部分
        registry.addInterceptor(new HandlerInterceptor() {
                    @Override
                    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                        String clientToken = SaOAuth2Manager.getDataResolver().readClientToken(SaHolder.getRequest());
                        // 校验客户端token
                        SaOAuth2Util.checkClientToken(clientToken);
                        String tenantId = request.getHeader("tenant_id");
                        if (tenantId != null) {
                            TenantContextHolder.setTenantId(Long.valueOf(tenantId));
                        }
                        try {
                            Long clientId = OAuth2Util.getClientId();
                            UserUtil.setCreateBy(String.valueOf(clientId));
                        }catch (Exception e){}
                        return HandlerInterceptor.super.preHandle(request, response, handler);
                    }
                })
                .excludePathPatterns(webProperties.getOpenApi().getExcludePathPatterns())
                .addPathPatterns(webProperties.getOpenApi().getPrefix() + "/**");

    }

    /**
     * 注册过滤器
     */
    @Bean
    public FilterRegistrationBean<ThreadLocalCleanupFilter> loggingFilter() {
        FilterRegistrationBean<ThreadLocalCleanupFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ThreadLocalCleanupFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }

    /**
     * 注册转换器
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        // 注册utc时间转换器
        registry.addConverter(UtcToLocalDateTimeDeserializer.instance);
        registry.addConverterFactory(jacksonEnumConverterFactory);
    }

}
