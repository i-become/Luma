package com.luma.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.luma.common.exception.system.ISystemException;
import com.luma.common.utils.MapstructUtil;
import com.luma.framework.config.SystemConfig;
import com.luma.system.domain.entity.SysTenant;
import com.luma.system.domain.entity.SysUser;
import com.luma.system.domain.vo.SysTenantAddReq;
import com.luma.system.domain.vo.SysTenantPageReq;
import com.luma.system.domain.vo.SysTenantPageResp;
import com.luma.system.enums.SysStatusEnum;
import com.luma.system.mapper.SysTenantMapper;
import com.luma.system.mapper.SysUserMapper;
import com.luma.system.service.SysTenantService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 针对表【sys_tenant(租户表)】的数据库操作Service实现
 * @author i-become
 */
@Service
public class SysTenantServiceImpl extends ServiceImpl<SysTenantMapper, SysTenant> implements SysTenantService {

    @Resource
    private SystemConfig systemConfig;

    @Resource
    private SysUserMapper sysUserMapper;

    @Override
    @Transactional(readOnly = true)
    public IPage<SysTenantPageResp> page(SysTenantPageReq req){
        return baseMapper.selectTenantPage(req.toMpPage(), req);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(SysTenantAddReq req){
        // 判断租户别名是否重复
        if (lambdaQuery().eq(SysTenant::getAlias, req.getAlias()).exists()){
            throw new ISystemException("exception.tenant.alias.exists");
        }
        SysTenant sysTenant = MapstructUtil.convert(req, SysTenant.class);
        baseMapper.insert(sysTenant);
        // 给租户添加一个默认用户

        return sysTenant.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(Long id, SysTenantAddReq req){
        if (!lambdaQuery().eq(SysTenant::getId, id).exists()){
            throw new ISystemException("exception.tenant.notFound");
        }
        // 判断租户别名是否重复
        if (lambdaQuery().eq(SysTenant::getAlias, req.getAlias()).ne(SysTenant::getId, id).exists()){
            throw new ISystemException("exception.tenant.alias.exists");
        }
        SysTenant sysTenant = MapstructUtil.convert(req, SysTenant.class);
        sysTenant.setId(id);
        baseMapper.updateById(sysTenant);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(Long id){
        if (!lambdaQuery().eq(SysTenant::getId, id).exists()){
            throw new ISystemException("exception.tenant.notFound");
        }
        // 系统租户不能删除
        if (systemConfig.getSystemTenantId().equals(id)){
            throw new ISystemException("exception.tenant.system.cannotDelete");
        }
        // 判断租户下是否存在用户
        if (ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUser::getTenantId, id).exists()){
            throw new ISystemException("exception.tenant.hasUsers.cannotDelete");
        }
        baseMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, SysStatusEnum status){
        if (!lambdaQuery().eq(SysTenant::getId, id).exists()){
            throw new ISystemException("exception.tenant.notFound");
        }
        // 系统租户不能停用
        if (systemConfig.getSystemTenantId().equals(id) && status == SysStatusEnum.DISABLED){
            throw new ISystemException("exception.tenant.system.cannotDisable");
        }
        lambdaUpdate().set(SysTenant::getStatus, status).eq(SysTenant::getId, id).update();
    }

}
