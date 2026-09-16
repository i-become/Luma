package com.luma.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.luma.system.domain.entity.SysTenant;
import com.luma.system.domain.vo.SysTenantAddReq;
import com.luma.system.domain.vo.SysTenantPageReq;
import com.luma.system.domain.vo.SysTenantPageResp;
import com.luma.system.enums.SysStatusEnum;

/**
 * 针对表【sys_tenant(租户表)】的数据库操作Service
 * @author i-become
 */
public interface SysTenantService extends IService<SysTenant> {

    /**
     * 获取租户分页
     * @param req 查询信息
     * @return 租户分页
     */
    IPage<SysTenantPageResp> page(SysTenantPageReq req);

    /**
     * 添加租户
     * @param req 租户信息
     * @return 租户编号
     */
    Long add(SysTenantAddReq req);

    /**
     * 编辑租户
     * @param id 租户编号
     * @param req 租户信息
     */
    void edit(Long id, SysTenantAddReq req);

    /**
     * 删除租户
     * @param id 租户编号
     */
    void remove(Long id);

    /**
     * 更新租户状态
     * @param id 租户编号
     * @param status 租户状态
     */
    void updateStatus(Long id, SysStatusEnum status);

}
