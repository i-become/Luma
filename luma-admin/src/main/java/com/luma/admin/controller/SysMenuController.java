package com.luma.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.luma.system.domain.vo.SysMenuAddReq;
import com.luma.system.domain.vo.SysMenuBaseListResp;
import com.luma.system.domain.vo.SysMenuListResp;
import com.luma.system.service.SysMenuService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单相关接口
 * @author i-become
 */
@Validated
@RestController
@RequestMapping("/sys/menu")
public class SysMenuController {

    @Resource
    private SysMenuService sysMenuService;

    /**
     * 获取当前用户的菜单列表
     * @param name 菜单名称 模糊搜索
     * @return 菜单列表
     */
    @GetMapping
    public List<SysMenuListResp> list(String name){
        return sysMenuService.list(name);
    }

    /**
     * 获取指定角色的菜单列表
     * @param roleId 角色编号
     * @return 菜单列表
     */
    @GetMapping("/list")
    public List<SysMenuBaseListResp> baseList(@NotNull(message = "{validation.role.id.NotNull}") Long roleId){
        return sysMenuService.baseList(roleId);
    }

    /**
     * 添加菜单
     * @param req 菜单信息
     * @return 菜单编号
     */
    @PostMapping
    @SaCheckPermission("system:menu:add")
    public Long add(@Valid @RequestBody SysMenuAddReq req){
        return sysMenuService.add(req);
    }

    /**
     * 删除菜单
     * @param id 菜单编号
     */
    @DeleteMapping("/{id}")
    @SaCheckPermission("system:menu:remove")
    public void remove(@NotNull(message = "{validation.menu.id.NotNull}") @PathVariable Long id){
        sysMenuService.removeById(id);
    }

    /**
     * 修改菜单
     * @param id 菜单编号
     * @param req 菜单信息
     */
    @PutMapping("/{id}")
    @SaCheckPermission("system:menu:edit")
    public void edit(@NotNull(message = "{validation.menu.id.NotNull}") @PathVariable Long id, @RequestBody SysMenuAddReq req){
        sysMenuService.edit(id, req);
    }


}
