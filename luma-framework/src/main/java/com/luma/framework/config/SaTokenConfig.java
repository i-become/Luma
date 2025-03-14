package com.luma.framework.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.oauth2.consts.SaOAuth2Consts;
import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.data.model.ClientTokenModel;
import cn.dev33.satoken.oauth2.scope.handler.SaOAuth2ScopeHandlerInterface;
import cn.dev33.satoken.stp.StpLogic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * saToken配置
 */
@Configuration
public class SaTokenConfig {

    // Sa-Token 整合 jwt (Simple 简单模式)
    @Bean
    public StpLogic getStpLogicJwt() {
        return new StpLogicJwtForSimple();
    }

    /**
     * 最终权限处理器
     * @return
     */
    @Bean
    public SaOAuth2ScopeHandlerInterface getSaOAuth2ScopeHandler() {
        return new SaOAuth2ScopeHandlerInterface() {

            @Override
            public String getHandlerScope() {
                return SaOAuth2Consts._FINALLY_WORK_SCOPE;
            }

            @Override
            public void workAccessToken(AccessTokenModel at) {

            }

            @Override
            public void workClientToken(ClientTokenModel ct) {
                // 注入租户编号，源码中这一步执行在持久化之前
                SaRequest req = SaHolder.getRequest();
                String tenantId = req.getParam("tenant_id");
                ct.extraData.put("tenant_id", tenantId);
            }
        };
    }

}
