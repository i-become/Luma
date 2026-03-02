package com.luma.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.luma.system.domain.vo.*;
import com.luma.system.enums.SysStatusEnum;
import com.luma.system.service.SysDeptService;
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

    @Resource
    private SysDeptService sysDeptService;

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
     * 更新角色状态
     * @param id 角色编号
     * @param status 状态值
     */
    @PatchMapping("/{id}/status")
    @SaCheckPermission("system:role:edit")
    public void updateStatus(@NotNull(message = "{validation.role.id.NotNull}") @PathVariable Long id, 
                              @NotNull @RequestParam SysStatusEnum status){
        sysRoleService.updateStatus(id, status);
    }

    /**
     * 获取指定角色的菜单列表
     * @param id 角色编号
     * @return 菜单列表
     */
    @GetMapping("/{id}/menus")
    public List<SysMenuBaseListResp> menus(@NotNull(message = "{validation.role.id.NotNull}") @PathVariable Long id){
        return sysMenuService.getBaseListByRoleId(id);
    }

    /**
     * 获取角色部门列表
     * @param id 角色编号
     * @return 部门列表
     */
    @GetMapping("/{id}/depts")
    public List<SysDeptBaseListResp> depts(@PathVariable Long id){
        return sysDeptService.getBaseListByRoleId(id);
    }

}
