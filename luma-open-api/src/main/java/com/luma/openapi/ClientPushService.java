package com.luma.openapi;

import cn.dev33.satoken.util.SaFoxUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.symmetric.SM4;
import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.luma.common.utils.JsonUtil;
import com.luma.system.domain.bo.ClientKeyBo;
import com.luma.system.service.ClientService;
import com.luma.system.service.ClientSysTenantService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 推送给应用的服务类
 * @author i-become
 */
@Slf4j
@Component
public class ClientPushService {

    @Resource
    private ClientService clientService;

    @Resource
    private ClientSysTenantService clientSysTenantService;

    /**
     * 推送给租户所有的应用
     * @param tenantId
     * @param method
     * @param data
     */
    public void pushAll(Long tenantId, String method, Object data){
        List<ClientKeyBo> list = clientSysTenantService.getClientsByTenantId(tenantId);
        for (ClientKeyBo client : list) {
            if (StringUtils.isBlank(client.getPushUrl())) {
                continue;
            }
            try {
                push(tenantId, client.getId(), client.getSecret(), client.getPushUrl(), method, data);
            }catch (Exception e){
                log.error("第三方数据推送异常：{}", e.getMessage());
            }
        }
    }

    /**
     * 推送给单个应用
     * @param clientId
     * @param method
     * @param data
     */
    public void push(Long tenantId, Long clientId, String method, Object data){
        // 获取客户端的密钥和推送地址
        ClientKeyBo client = clientService.getClientKey(clientId);
        push(tenantId, clientId, client.getSecret(), client.getPushUrl(), method, data);
    }

    public void push(Long tenantId, Long clientId, String secret, String pushUrl, String method, Object data){
        // 首先将数据转成json字符串
        String json = JsonUtil.toJsonStringNonNull(data);
        log.info("推送数据：{}", json);
        // 使用sm4对明文进行对称加密
        String key = SmUtil.sm3(clientId + secret);
        byte[] sm4Key = Arrays.copyOf(key.getBytes(StandardCharsets.UTF_8), 16);
        SM4 sm4 = SmUtil.sm4(sm4Key);
        String hex = sm4.encryptHex(json);
        // 参数 & 签名
        Map<String, Object> paramsMap = new TreeMap<>();
        paramsMap.put("data", hex);
        paramsMap.put("method", method);
        paramsMap.put("tenantId", tenantId);
        paramsMap.put("clientId", clientId);
        paramsMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
        paramsMap.put("nonceStr", SaFoxUtil.getRandomString(32));
        paramsMap.put("sign", SecureUtil.signParamsMd5(paramsMap, "&key=" + secret));
        // 推送到第三方
        HttpUtil.createPost(pushUrl).body(JsonUtil.toJsonStringNonNull(paramsMap)).executeAsync();
    }

}
