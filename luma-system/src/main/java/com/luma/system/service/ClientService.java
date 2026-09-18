package com.luma.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.luma.system.domain.bo.ClientKeyBo;
import com.luma.system.domain.entity.Client;
import com.luma.system.domain.vo.*;

import java.util.List;

/**
 * 针对表【client(应用表)】的数据库操作Service
 * @author i-become
 */
public interface ClientService extends IService<Client> {

    /**
     * 获取应用分页
     * @param req 查询信息
     * @return 应用分页
     */
    IPage<ClientPageResp> page(ClientPageReq req);

    /**
     * 获取应用详情
     * @param id 应用编号
     * @return 应用详情
     */
    ClientInfoResp info(Long id);

    /**
     * 获取应用基础列表
     * @return 应用列表
     */
    List<ClientBaseListResp> getBaseList();

    /**
     * 添加应用
     * @param req 应用信息
     * @return 应用编号
     */
    Long add(ClientAddReq req);

    /**
     * 编辑应用
     * @param id 应用编号
     * @param req 应用信息
     */
    void edit(Long id, ClientAddReq req);

    /**
     * 删除应用
     * @param id 应用编号
     */
    void remove(Long id);

    /**
     * 重置应用密钥
     * @param id 应用编号
     * @return 新密钥信息
     */
    ClientSecretResp resetSecret(Long id);

    /**
     * 获取客户端密钥和推送地址等信息
     * @param id 客户端编号
     * @return 应用密钥信息
     */
    ClientKeyBo getClientKey(Long id);

    /**
     * 获取所有客户端密钥和推送地址等信息
     * @return 应用密钥列表
     */
    List<ClientKeyBo> getClientKeyAll();
}
