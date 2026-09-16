package com.luma.system.auth;

import cn.dev33.satoken.oauth2.data.loader.SaOAuth2DataLoader;
import cn.dev33.satoken.oauth2.data.model.loader.SaClientModel;
import cn.dev33.satoken.stp.StpUtil;
import com.luma.common.constant.SatokenConstant;
import com.luma.common.exception.system.ISystemException;
import com.luma.system.domain.bo.SysUserLoginClient;
import com.luma.system.domain.entity.Client;
import com.luma.system.domain.entity.ClientSysUser;
import com.luma.system.service.ClientService;
import com.luma.system.service.ClientSysUserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Sa-Token OAuth2 定制化配置
 * 加载client和生成openId
 */
@Component
public class SaOAuth2DataLoaderImpl implements SaOAuth2DataLoader {

    @Resource
    private ClientService clientService;

    @Resource
    private ClientSysUserService clientSysUserService;

    @Override
    public SaClientModel getClientModel(String clientId) {

        Client client = clientService.getById(Long.valueOf(clientId));

        return new SaClientModel()
                .setClientId(clientId)
                .setClientSecret(client.getSecret())
                .addAllowRedirectUris(client.getAllowUrl().split(","))
                .addContractScopes((client.getScope() + "," + client.getSysScope()).split(","))
                .addAllowGrantTypes(client.getGrantType().split(","));

    }

    @Override
    public String getOpenid(String clientId, Object loginId) {
        // 判断该用户是否有这个应用权限
        boolean exists = clientSysUserService.lambdaQuery()
                .eq(ClientSysUser::getClientId, clientId)
                .eq(ClientSysUser::getUserId, Long.valueOf(String.valueOf(loginId)))
                .exists();
        if (!exists){
            throw new ISystemException("exception.oauth.noAppPermission");
        }
        // 关联用户登录应用
        String userId = String.valueOf(loginId);
        synchronized (userId.intern()){
            String loginClientKey = SatokenConstant.getOauth2LoginClientKey(userId);
            List<SysUserLoginClient> loginClientList = (List<SysUserLoginClient>) StpUtil.stpLogic.getSaTokenDao().getObject(loginClientKey);
            if (loginClientList == null){
                loginClientList = new ArrayList<>();
            }
            Long clientIdAsLong = Long.valueOf(clientId);
            // 如果没有关联过则从数据库查找并关联
            if (loginClientList.stream().noneMatch(o -> o.getClientId().equals(clientIdAsLong))){
                Client client = clientService.lambdaQuery().select(Client::getId, Client::getSecret, Client::getLogoutCall).eq(Client::getId, clientIdAsLong).one();
                SysUserLoginClient sysUserLoginClient = new SysUserLoginClient();
                sysUserLoginClient.setClientId(clientIdAsLong);
                sysUserLoginClient.setSecret(client.getSecret());
                sysUserLoginClient.setLogoutCall(client.getLogoutCall());
                loginClientList.add(sysUserLoginClient);
                StpUtil.stpLogic.getSaTokenDao().setObject(loginClientKey, loginClientList, -1);
            }
        }
        return SaOAuth2DataLoader.super.getOpenid(clientId, loginId);
    }

}
