package com.luma.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luma.system.domain.bo.ClientKeyBo;
import com.luma.system.domain.entity.ClientSysTenant;
import com.luma.system.mapper.ClientSysTenantMapper;
import com.luma.system.service.ClientSysTenantService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClientSysTenantServiceImpl extends ServiceImpl<ClientSysTenantMapper, ClientSysTenant> implements ClientSysTenantService {

    @Override
    @Transactional(readOnly = true)
//    @Cacheable(value = "client:ByTenantId", key = "#tenantId")
    public List<ClientKeyBo> getClientsByTenantId(Long tenantId) {
        return baseMapper.selectClientsByTenantId(tenantId);
    }

}
