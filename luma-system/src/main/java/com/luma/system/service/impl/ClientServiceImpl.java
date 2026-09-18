package com.luma.system.service.impl;

import cn.dev33.satoken.util.SaFoxUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.luma.common.exception.system.ISystemException;
import com.luma.common.utils.MapstructUtil;
import com.luma.system.domain.bo.ClientKeyBo;
import com.luma.system.domain.entity.Client;
import com.luma.system.domain.entity.ClientSysTenant;
import com.luma.system.domain.entity.ClientSysUser;
import com.luma.system.domain.entity.SysTenant;
import com.luma.system.domain.entity.SysUser;
import com.luma.system.domain.vo.*;
import com.luma.system.mapper.ClientMapper;
import com.luma.system.mapper.ClientSysTenantMapper;
import com.luma.system.mapper.ClientSysUserMapper;
import com.luma.system.mapper.SysTenantMapper;
import com.luma.system.mapper.SysUserMapper;
import com.luma.system.service.ClientService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 针对表【client(应用表)】的数据库操作Service实现
 * @author i-become
 */
@Service
public class ClientServiceImpl extends ServiceImpl<ClientMapper, Client> implements ClientService {

    @Resource
    private ClientSysUserMapper clientSysUserMapper;

    @Resource
    private ClientSysTenantMapper clientSysTenantMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private SysTenantMapper sysTenantMapper;

    @Override
    @Transactional(readOnly = true)
    public IPage<ClientPageResp> page(ClientPageReq req){
        return baseMapper.selectClientPage(req.toMpPage(), req);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientInfoResp info(Long id){
        Client client = lambdaQuery().eq(Client::getId, id).one();
        if (client == null){
            throw new ISystemException("exception.client.notFound");
        }
        ClientInfoResp resp = MapstructUtil.convert(client, ClientInfoResp.class);
        resp.setUserIds(selectUserIdsByClientId(id));
        resp.setTenantIds(selectTenantIdsByClientId(id));
        return resp;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientBaseListResp> getBaseList(){
        return baseMapper.selectClientBaseList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(ClientAddReq req){
        if (lambdaQuery().eq(Client::getName, req.getName()).exists()){
            throw new ISystemException("exception.client.name.exists");
        }
        checkAssignData(req.getUserIds(), req.getTenantIds());
        Client client = MapstructUtil.convert(req, Client.class);
        client.setSecret(generateSecret());
        baseMapper.insert(client);
        saveClientUsers(client.getId(), req.getUserIds());
        saveClientTenants(client.getId(), req.getTenantIds());
        return client.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(Long id, ClientAddReq req){
        Client client = lambdaQuery().eq(Client::getId, id).one();
        if (client == null){
            throw new ISystemException("exception.client.notFound");
        }
        if (lambdaQuery().eq(Client::getName, req.getName()).ne(Client::getId, id).exists()){
            throw new ISystemException("exception.client.name.exists");
        }
        checkAssignData(req.getUserIds(), req.getTenantIds());
        MapstructUtil.convert(req, client);
        baseMapper.updateById(client);
        if (req.getUserIds() != null){
            ChainWrappers.lambdaUpdateChain(clientSysUserMapper).eq(ClientSysUser::getClientId, id).remove();
            saveClientUsers(id, req.getUserIds());
        }
        if (req.getTenantIds() != null){
            ChainWrappers.lambdaUpdateChain(clientSysTenantMapper).eq(ClientSysTenant::getClientId, id).remove();
            saveClientTenants(id, req.getTenantIds());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(Long id){
        if (!lambdaQuery().eq(Client::getId, id).exists()){
            throw new ISystemException("exception.client.notFound");
        }
        baseMapper.deleteById(id);
        ChainWrappers.lambdaUpdateChain(clientSysUserMapper).eq(ClientSysUser::getClientId, id).remove();
        ChainWrappers.lambdaUpdateChain(clientSysTenantMapper).eq(ClientSysTenant::getClientId, id).remove();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ClientSecretResp resetSecret(Long id){
        if (!lambdaQuery().eq(Client::getId, id).exists()){
            throw new ISystemException("exception.client.notFound");
        }
        String secret = generateSecret();
        lambdaUpdate().set(Client::getSecret, secret).eq(Client::getId, id).update();
        ClientSecretResp resp = new ClientSecretResp();
        resp.setId(id);
        resp.setSecret(secret);
        return resp;
    }

    @Override
    public ClientKeyBo getClientKey(Long id) {
        Client client = lambdaQuery().select(Client::getId, Client::getSecret, Client::getPushUrl).eq(Client::getId, id).one();
        return MapstructUtil.convert(client, ClientKeyBo.class);
    }

    @Override
    public List<ClientKeyBo> getClientKeyAll() {
        return lambdaQuery().select(Client::getId, Client::getSecret, Client::getPushUrl).list().stream().map(o -> MapstructUtil.convert(o, ClientKeyBo.class)).toList();
    }

    /**
     * 校验授权的用户、租户是否存在
     * @param userIds 用户编号列表
     * @param tenantIds 租户编号列表
     */
    private void checkAssignData(Set<Long> userIds, Set<Long> tenantIds){
        if (userIds != null && !userIds.isEmpty()){
            Long count = ChainWrappers.lambdaQueryChain(sysUserMapper).in(SysUser::getId, userIds).count();
            if (count != userIds.size()){
                throw new ISystemException("exception.client.user.invalid");
            }
        }
        if (tenantIds != null && !tenantIds.isEmpty()){
            Long count = ChainWrappers.lambdaQueryChain(sysTenantMapper).in(SysTenant::getId, tenantIds).count();
            if (count != tenantIds.size()){
                throw new ISystemException("exception.client.tenant.invalid");
            }
        }
    }

    /**
     * 保存应用与用户关联
     * @param clientId 应用编号
     * @param userIds 用户编号列表
     */
    private void saveClientUsers(Long clientId, Set<Long> userIds){
        if (userIds == null || userIds.isEmpty()){
            return;
        }
        List<ClientSysUser> list = new ArrayList<>();
        for (Long userId : userIds){
            ClientSysUser row = new ClientSysUser();
            row.setClientId(clientId);
            row.setUserId(userId);
            list.add(row);
        }
        clientSysUserMapper.insert(list);
    }

    /**
     * 保存应用与租户关联
     * @param clientId 应用编号
     * @param tenantIds 租户编号列表
     */
    private void saveClientTenants(Long clientId, Set<Long> tenantIds){
        if (tenantIds == null || tenantIds.isEmpty()){
            return;
        }
        List<ClientSysTenant> list = new ArrayList<>();
        for (Long tenantId : tenantIds){
            ClientSysTenant row = new ClientSysTenant();
            row.setClientId(clientId);
            row.setTenantId(tenantId);
            list.add(row);
        }
        clientSysTenantMapper.insert(list);
    }

    /**
     * 查询应用已授权的用户编号
     * @param clientId 应用编号
     * @return 用户编号集合
     */
    private Set<Long> selectUserIdsByClientId(Long clientId){
        return ChainWrappers.lambdaQueryChain(clientSysUserMapper)
                .select(ClientSysUser::getUserId)
                .eq(ClientSysUser::getClientId, clientId)
                .list()
                .stream()
                .map(ClientSysUser::getUserId)
                .collect(Collectors.toCollection(HashSet::new));
    }

    /**
     * 查询应用已授权的租户编号
     * @param clientId 应用编号
     * @return 租户编号集合
     */
    private Set<Long> selectTenantIdsByClientId(Long clientId){
        return ChainWrappers.lambdaQueryChain(clientSysTenantMapper)
                .select(ClientSysTenant::getTenantId)
                .eq(ClientSysTenant::getClientId, clientId)
                .list()
                .stream()
                .map(ClientSysTenant::getTenantId)
                .collect(Collectors.toCollection(HashSet::new));
    }

    /**
     * 生成应用密钥
     * @return 随机密钥
     */
    private static String generateSecret(){
        return SaFoxUtil.getRandomString(32);
    }

}
