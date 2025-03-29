package com.luma.framework.utils;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.template.SaOAuth2Util;

/**
 * @author i-become
 */
public class OAuth2Util {

    /**
     * 获取当前请求的clientId
     * @return
     */
    public static Long getClientId(){
        String clientToken = SaOAuth2Manager.getDataResolver().readClientToken(SaHolder.getRequest());
        return Long.valueOf(SaOAuth2Util.getClientToken(clientToken).getClientId());
    }

}
