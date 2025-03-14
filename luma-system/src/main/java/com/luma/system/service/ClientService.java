package com.luma.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.luma.system.domain.bo.ClientKeyBo;
import com.luma.system.domain.entity.Client;

import java.util.List;

public interface ClientService extends IService<Client> {

    /**
     * 获取客户端密钥和推送地址等信息
     * @param id 客户端编号
     * @return
     */
    ClientKeyBo getClientKey(Long id);

    /**
     * 获取所有客户端密钥和推送地址等信息
     * @return
     */
    List<ClientKeyBo> getClientKeyAll();
}
