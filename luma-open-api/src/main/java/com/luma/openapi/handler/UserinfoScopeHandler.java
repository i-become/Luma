package com.luma.openapi.handler;

import cn.dev33.satoken.oauth2.data.loader.SaOAuth2DataLoader;
import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.data.model.ClientTokenModel;
import cn.dev33.satoken.oauth2.scope.handler.SaOAuth2ScopeHandlerInterface;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.symmetric.SM4;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.luma.framework.permission.TenantContextHolder;
import com.luma.system.domain.entity.Client;
import com.luma.system.domain.entity.SysUser;
import com.luma.system.service.ClientService;
import com.luma.system.service.SysUserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * saToken中对于client获取用户accessToken时为userinfo权限的用户信息
 */
@Component
public class UserinfoScopeHandler implements SaOAuth2ScopeHandlerInterface {

    @Resource
    private SysUserService sysUserService;

    @Resource
    private ClientService clientService;

    @Resource
    private SaOAuth2DataLoader saOAuth2DataLoader;

    @Override
    public String getHandlerScope() {
        return "userinfo";
    }

    @Override
    public void workAccessToken(AccessTokenModel at) {
        TenantContextHolder.disableNext();
        SysUser user = sysUserService.lambdaQuery().select(SysUser::getId, SysUser::getNickname, SysUser::getLoginName, SysUser::getPhone, SysUser::getTenantId).eq(SysUser::getId, Long.valueOf(String.valueOf(at.getLoginId()))).one();
        Client client = clientService.lambdaQuery().select(Client::getId, Client::getSecret).eq(Client::getId, Long.valueOf(at.getClientId())).one();
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("userId", user.getId());
        map.put("openId", saOAuth2DataLoader.getOpenid(at.getClientId(), at.getLoginId()));
        map.put("nickname", user.getNickname());
        map.put("loginName", user.getLoginName());
        map.put("tenantId", user.getTenantId());
        if (StringUtils.isNotBlank(user.getPhone())){
            // 对手机号码进行加密处理，先使用SM3摘要算法对 （客户端编号 + 客户端密钥） 进行摘要加密，随后使用密文对手机号码进行SM4对称加密
            String key = SmUtil.sm3(client.getId() + client.getSecret());
            // 截取前16字节作为SM4密钥
            byte[] sm4Key = Arrays.copyOf(key.getBytes(StandardCharsets.UTF_8), 16);
            SM4 sm4 = SmUtil.sm4(sm4Key);
            String phoneHex = sm4.encryptHex(user.getPhone());
            map.put("phone", phoneHex);
        }else {
            map.put("phone", null);
        }
        at.extraData.put("userinfo", map);

    }

    @Override
    public void workClientToken(ClientTokenModel ct) {

    }

}
