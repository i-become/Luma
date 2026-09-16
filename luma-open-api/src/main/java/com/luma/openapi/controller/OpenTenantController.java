package com.luma.openapi.controller;

import com.luma.common.exception.system.ISystemException;
import com.luma.common.utils.MapstructUtil;
import com.luma.openapi.domain.vo.SysTenantInfoResp;
import com.luma.system.domain.entity.SysTenant;
import com.luma.system.service.SysTenantService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 租户相关接口
 * @author i-become
 */
@RestController
@RequestMapping("/sys/tenant")
public class OpenTenantController {

    @Resource
    private SysTenantService sysTenantService;

    /**
     * 获取租户信息
     * @param id 租户编号
     * @return
     */
    @GetMapping
    public SysTenantInfoResp info(@RequestParam Long id) {
        SysTenant sysTenant = sysTenantService.lambdaQuery().select(SysTenant::getId, SysTenant::getAlias, SysTenant::getName, SysTenant::getStatus).eq(SysTenant::getId, id).one();
        if (sysTenant == null) {
            throw new ISystemException("exception.tenant.notFound");
        }
        return MapstructUtil.convert(sysTenant, SysTenantInfoResp.class);
    }

}
