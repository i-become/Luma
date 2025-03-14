package com.luma.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luma.common.utils.MapstructUtil;
import com.luma.system.domain.bo.ClientKeyBo;
import com.luma.system.domain.entity.Client;
import com.luma.system.mapper.ClientMapper;
import com.luma.system.service.ClientService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientServiceImpl extends ServiceImpl<ClientMapper, Client> implements ClientService {

    @Override
    public ClientKeyBo getClientKey(Long id) {
        Client client = lambdaQuery().select(Client::getId, Client::getSecret, Client::getPushUrl).eq(Client::getId, id).one();
        return MapstructUtil.convert(client, ClientKeyBo.class);
    }

    @Override
    public List<ClientKeyBo> getClientKeyAll() {
        return lambdaQuery().select(Client::getId, Client::getSecret, Client::getPushUrl).list().stream().map(o -> MapstructUtil.convert(o, ClientKeyBo.class)).toList();
    }

}
