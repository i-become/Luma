package com.luma.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.luma.system.domain.bo.ClientKeyBo;
import com.luma.system.domain.entity.ClientSysTenant;

import java.util.List;

public interface ClientSysTenantService extends IService<ClientSysTenant> {

    /**
     * 获取租户安装的应用列表
     * @param tenantId 租户编号
     * @return 应用密钥列表
     */
    List<ClientKeyBo> getClientsByTenantId(Long tenantId);

}
