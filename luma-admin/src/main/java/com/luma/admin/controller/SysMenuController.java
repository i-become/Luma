package com.luma.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.luma.common.utils.MapstructUtil;
import com.luma.system.domain.entity.SysMenu;
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
@RequestMapping("/menu")
public class SysMenuController {

    @Resource
    private SysMenuService sysMenuService;

    /**
     * 获取系统菜单列表
     * @return 菜单列表
     */
    @GetMapping
    @SaCheckPermission("system:menu:query")
    public List<SysMenuListResp> list(){
        List<SysMenu> list = sysMenuService.lambdaQuery().select(SysMenu::getId, SysMenu::getParentId, SysMenu::getName, SysMenu::getRedirect, SysMenu::getComponent,
                        SysMenu::getIcon, SysMenu::getSort, SysMenu::getTitle, SysMenu::getTarget, SysMenu::getActiveMenu, SysMenu::getType, SysMenu::getPath,
                        SysMenu::getLinkUrl, SysMenu::getHidden, SysMenu::getFullscreen, SysMenu::getAffix, SysMenu::getKeepAlive, SysMenu::getBadge, 
                        SysMenu::getBadgeType, SysMenu::getPerms, SysMenu::getStatus, SysMenu::getRemark)
                .list();
        return MapstructUtil.convert(list, SysMenuListResp.class);
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
        sysMenuService.remove(id);
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
