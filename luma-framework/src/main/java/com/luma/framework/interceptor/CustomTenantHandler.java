package com.luma.framework.interceptor;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.luma.framework.permission.TenantThreadLocal;
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
            return new LongValue(TenantThreadLocal.getTenantId());
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public boolean ignoreTable(String tableName) {
        Integer state = TenantThreadLocal.getState();
        if (state == null) {
            return ignoreTables.contains("," + tableName + ",");
        }else if (state == 0) {
            TenantThreadLocal.clearState();
            return true;
        }else if (state == 1) {
            TenantThreadLocal.clearState();
            return false;
        }else if (state == -1) {
            return true;
        }
        return false;
    }

}
