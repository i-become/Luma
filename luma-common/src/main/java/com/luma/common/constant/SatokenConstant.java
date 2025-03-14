package com.luma.common.constant;

/**
 * 自定义的satoken部分常量
 */
public class SatokenConstant {

    public static final String OAUTH2_LOGIN_CLIENT = "satoken:oauth2:user:login-client:";

    /**
     * 获取用户登录的所有应用的缓存key
     * @param loginId 登录id
     * @return
     */
    public static String getOauth2LoginClientKey(String loginId){
        return OAUTH2_LOGIN_CLIENT + loginId;
    }

}
