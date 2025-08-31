package com.luma.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.luma.framework.utils.UserUtil;
import com.luma.system.domain.vo.*;
import com.luma.system.enums.SysStatusEnum;
import com.luma.system.service.SysMenuService;
import com.luma.system.service.SysRoleService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统角色相关接口
 * @author i-become
 */
@Validated
@RestController
@RequestMapping("/role")
public class SysRoleController {

    @Resource
    private SysRoleService sysRoleService;

    @Resource
    private SysMenuService sysMenuService;

    /**
     * 角色分页
     * @param req 查询信息
     * @return 角色分页
     */
    @GetMapping
    @SaCheckPermission("system:role:query")
    public IPage<SysRolePageResp> page(SysRolePageReq req){
        return sysRoleService.page(req);
    }

    /**
     * 添加角色
     * @param req 角色信息
     * @return 角色编号
     */
    @PostMapping
    @SaCheckPermission("system:role:add")
    public Long add(@Valid @RequestBody SysRoleAddReq req){
        return sysRoleService.add(req);
    }

    /**
     * 编辑角色
     * @param id 角色编号
     * @param req 角色信息
     */
    @PutMapping("/{id}")
    @SaCheckPermission("system:role:edit")
    public void edit(@NotNull(message = "{validation.role.id.NotNull}") @PathVariable Long id, @Valid @RequestBody SysRoleAddReq req){
        sysRoleService.edit(id, req);
    }

    /**
     * 删除角色
     * @param id 角色编号
     */
    @DeleteMapping("/{id}")
    @SaCheckPermission("system:role:remove")
    public void remove(@NotNull(message = "{validation.role.id.NotNull}") @PathVariable Long id){
        sysRoleService.remove(id);
    }

    /**
     * 启用角色
     * @param id 角色编号
     */
    @PutMapping("/{id}/enable")
    @SaCheckPermission("system:role:edit")
    public void enable(@NotNull(message = "{validation.role.id.NotNull}") @PathVariable Long id){
        sysRoleService.updateStatus(id, SysStatusEnum.NORMAL);
    }

    /**
     * 禁用角色
     * @param id 角色编号
     */
    @PutMapping("/{id}/disable")
    @SaCheckPermission("system:role:edit")
    public void disable(@NotNull(message = "{validation.role.id.NotNull}") @PathVariable Long id){
        sysRoleService.updateStatus(id, SysStatusEnum.DISABLED);
    }

    /**
     * 获取指定角色的菜单列表
     * @param id 角色编号
     * @return 菜单列表
     */
    @GetMapping("/{id}/menus")
    public List<SysMenuBaseListResp> menus(@NotNull(message = "{validation.role.id.NotNull}") @PathVariable Long id){
        return sysMenuService.baseList(id);
    }

}
