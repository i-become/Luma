package com.luma.framework.interceptor;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.luma.framework.permission.TenantContextHolder;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * mybatis-plus多租户插件配置
 */
@Component
public class CustomTenantHandler implements TenantLineHandler {

    private String ignoreTables;

    @Value("${luma.tenant.ignore-tables}")
    public void setIgnoreTables(String ignoreTables) {
        this.ignoreTables = "," + ignoreTables + ",";
    }

    @Override
    public Expression getTenantId() {
        try {
            return new LongValue(TenantContextHolder.getTenantId());
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public boolean ignoreTable(String tableName) {
        // 如果是默认状态就采用配置的表判断
        if (TenantContextHolder.isDefaultState()){
            return ignoreTables.contains("," + tableName + ",");
        }
        // 如果是启用本次查询状态，就返回启用，并清除状态
        if (TenantContextHolder.isEnableNext()){
            TenantContextHolder.clearState();
            return false;
        }
        // 如果是忽略本次查询状态，就返回忽略，并清除状态
        if (TenantContextHolder.isDisableNext()) {
            TenantContextHolder.clearState();
            return true;
        }
        // 返回是否一直禁用多租户查询
        return TenantContextHolder.isDisable();
    }

}
