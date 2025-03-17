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
        if (TenantContextHolder.isDefaultState()){
            return ignoreTables.contains("," + tableName + ",");
        }
        if (TenantContextHolder.isEnableNext()){
            TenantContextHolder.clearState();
            return false;
        }
        if (TenantContextHolder.isDisableNext()) {
            TenantContextHolder.clearState();
            return true;
        }
        if (TenantContextHolder.isDisable()) {
            return true;
        }
        return false;
    }

}
