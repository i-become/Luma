package com.luma.system.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.luma.system.domain.entity.SysTenant;
import com.luma.system.domain.vo.SysTenantPageReq;
import com.luma.system.domain.vo.SysTenantPageResp;
import org.apache.ibatis.annotations.Param;

/**
 * 针对表【sys_tenant(租户表)】的数据库操作Mapper
 * @author i-become
 */
public interface SysTenantMapper extends BaseMapper<SysTenant> {

    /**
     * 查询租户分页
     * @param page 分页参数
     * @param req 查询条件
     * @return 租户分页
     */
    IPage<SysTenantPageResp> selectTenantPage(IPage page, @Param("req") SysTenantPageReq req);

}
