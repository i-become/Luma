package com.luma.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.luma.system.domain.vo.SysTenantAddReq;
import com.luma.system.domain.vo.SysTenantPageReq;
import com.luma.system.domain.vo.SysTenantPageResp;
import com.luma.system.enums.SysStatusEnum;
import com.luma.system.service.SysTenantService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 系统租户相关接口
 * @author i-become
 */
@Validated
@RestController
@RequestMapping("/tenant")
public class SysTenantController {

    @Resource
    private SysTenantService sysTenantService;

    /**
     * 租户分页
     * @param req 查询信息
     * @return 租户分页
     */
    @GetMapping
    @SaCheckPermission("system:tenant:query")
    public IPage<SysTenantPageResp> page(SysTenantPageReq req){
        return sysTenantService.page(req);
    }

    /**
     * 添加租户
     * @param req 租户信息
     * @return 租户编号
     */
    @PostMapping
    @SaCheckPermission("system:tenant:add")
    public Long add(@Valid @RequestBody SysTenantAddReq req){
        return sysTenantService.add(req);
    }

    /**
     * 编辑租户
     * @param id 租户编号
     * @param req 租户信息
     */
    @PutMapping("/{id}")
    @SaCheckPermission("system:tenant:edit")
    public void edit(@NotNull(message = "{validation.tenant.id.NotNull}") @PathVariable Long id, @Valid @RequestBody SysTenantAddReq req){
        sysTenantService.edit(id, req);
    }

    /**
     * 删除租户
     * @param id 租户编号
     */
    @DeleteMapping("/{id}")
    @SaCheckPermission("system:tenant:remove")
    public void remove(@NotNull(message = "{validation.tenant.id.NotNull}") @PathVariable Long id){
        sysTenantService.remove(id);
    }

    /**
     * 更新租户状态
     * @param id 租户编号
     * @param status 状态值
     */
    @PatchMapping("/{id}/status")
    @SaCheckPermission("system:tenant:edit")
    public void updateStatus(@NotNull(message = "{validation.tenant.id.NotNull}") @PathVariable Long id,
                             @NotNull @RequestParam SysStatusEnum status){
        sysTenantService.updateStatus(id, status);
    }

}
