package com.luma.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.luma.framework.utils.UserUtil;
import com.luma.system.domain.vo.SysRoleAddReq;
import com.luma.system.domain.vo.SysRoleBaseListResp;
import com.luma.system.domain.vo.SysRolePageReq;
import com.luma.system.domain.vo.SysRolePageResp;
import com.luma.system.enums.SysStatusEnum;
import com.luma.system.service.SysRoleService;
import jakarta.annotation.Resource;
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
@RequestMapping("/sys/role")
public class SysRoleController {

    @Resource
    private SysRoleService sysRoleService;

    /**
     * 角色分页
     * @param req
     * @return
     */
    @GetMapping("/page")
    @SaCheckPermission("system:role:query")
    public IPage<SysRolePageResp> page(SysRolePageReq req){
        return sysRoleService.getRolePage(req);
    }

    /**
     * 角色列表 （简易列表，主要用于下拉框）
     * @return
     */
    @GetMapping("/base_list")
    public List<SysRoleBaseListResp> list(){
        return sysRoleService.getRoleList(null);
    }

    /**
     * 获取指定用户角色列表 （简易列表，主要用于下拉框）
     * @return
     */
    @GetMapping("/user_role/base_list")
    public List<SysRoleBaseListResp> list(@NotNull(message = "userId不能为空") Long userId){
        return sysRoleService.getRoleList(userId);
    }

    /**
     * 添加角色
     * @param req
     */
    @PostMapping
    @SaCheckPermission("system:role:add")
    public void add(@RequestBody SysRoleAddReq req){
        sysRoleService.add(req);
    }

    /**
     * 编辑角色
     * @param id 角色编号
     * @param req
     */
    @PutMapping("/{id}")
    @SaCheckPermission("system:role:edit")
    public void edit(@PathVariable Long id, @RequestBody SysRoleAddReq req){
        sysRoleService.edit(id, req);
    }

    /**
     * 删除角色
     * @param id 角色编号
     */
    @DeleteMapping
    @SaCheckPermission("system:role:remove")
    public void remove(Long id){
        sysRoleService.remove(id);
    }

    /**
     * 启用角色
     * @param id 角色编号
     */
    @PostMapping("/enable")
    @SaCheckPermission("system:role:edit")
    public void enable(Long id){
        sysRoleService.updateStatus(id, SysStatusEnum.NORMAL);
    }

    /**
     * 禁用角色
     * @param id 角色编号
     */
    @PostMapping("/disable")
    @SaCheckPermission("system:role:edit")
    public void disable(Long id){
        sysRoleService.updateStatus(id, SysStatusEnum.DISABLED);
    }

}
