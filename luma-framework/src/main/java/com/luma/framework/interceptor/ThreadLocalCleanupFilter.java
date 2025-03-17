package com.luma.framework.interceptor;
import com.luma.framework.permission.DataScopeThreadLocal;
import com.luma.framework.permission.PermissionThreadLocal;
import com.luma.framework.permission.TenantContextHolder;
import com.luma.framework.utils.UserUtil;
import jakarta.servlet.*;

import java.io.IOException;

/**
 * 全局ThreadLocal清理过滤器
 * @author 刘靖
 */
public class ThreadLocalCleanupFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            // 继续执行请求
            chain.doFilter(request, response);
        } finally {
            // 清理 ThreadLocal 内容
            cleanupThreadLocal();
        }
    }

    /**
     * 清理 ThreadLocal 中的内容
     */
    private void cleanupThreadLocal() {
        // 这里清除所有 ThreadLocal 中的内容
        TenantContextHolder.clear();
        PermissionThreadLocal.cleanPermission();
        DataScopeThreadLocal.clean();
        UserUtil.cleanCreateBy();
    }

}
