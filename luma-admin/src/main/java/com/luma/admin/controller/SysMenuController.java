package com.luma.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.luma.system.domain.vo.SysMenuAddReq;
import com.luma.system.domain.vo.SysMenuBaseListResp;
import com.luma.system.domain.vo.SysMenuListResp;
import com.luma.system.service.SysMenuService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单相关接口
 * @author i-become
 */
@RestController
@RequestMapping("/sys/menu")
public class SysMenuController {

    @Resource
    private SysMenuService sysMenuService;

//    /**
//     * 获取当前登录用户自己的菜单树
//     * @return
//     */
//    @GetMapping("/trees")
//    public List<Tree<Long>> trees(){
//        return sysMenuService.trees();
//    }

    /**
     * 获取当前用户的菜单列表
     * @param name 菜单名称 模糊搜索
     * @return
     */
    @GetMapping("/list")
    public List<SysMenuListResp> list(String name){
        return sysMenuService.list(name);
    }

    /**
     * 获取指定角色的菜单列表
     * @param roleId 角色编号
     * @return
     */
    @GetMapping("/base_list")
    public List<SysMenuBaseListResp> baseList(Long roleId){
        return sysMenuService.baseList(roleId);
    }

    /**
     * 添加菜单
     * @param req
     */
    @PostMapping
    @SaCheckPermission("system:menu:add")
    public void add(@Validated @RequestBody SysMenuAddReq req){
        sysMenuService.add(req);
    }

    /**
     * 删除菜单
     * @param id 菜单编号
     */
    @DeleteMapping
    @SaCheckPermission("system:menu:remove")
    public void remove(Long id){
        sysMenuService.removeById(id);
    }

    /**
     * 修改菜单
     * @param id 菜单编号
     * @param req
     */
    @PutMapping("/{id}")
    @SaCheckPermission("system:menu:edit")
    public void edit(@PathVariable Long id, @Validated @RequestBody SysMenuAddReq req){
        sysMenuService.edit(id, req);
    }


}
