package com.luma.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.luma.system.domain.bo.ClientKeyBo;
import com.luma.system.domain.entity.ClientSysTenant;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ClientSysTenantMapper extends BaseMapper<ClientSysTenant> {

    /**
     * 查询租户安装的应用列表
     * @param tenantId 租户编号
     * @return
     */
    List<ClientKeyBo> selectClientsByTenantId(@Param("tenantId") Long tenantId);

}
