package com.luma.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.luma.framework.utils.UserUtil;
import com.luma.system.domain.entity.SysUser;
import com.luma.system.domain.vo.*;
import com.luma.system.enums.SysStatusEnum;
import com.luma.system.service.SysMenuService;
import com.luma.system.service.SysRoleService;
import com.luma.system.service.SysUserService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * 系统用户相关接口
 * @author i-become
 */
@Validated
@RestController
@RequestMapping("/user")
public class SysUserController {

    @Resource
    private SysUserService sysUserService;

    @Resource
    private SysRoleService sysRoleService;

    @Resource
    private SysMenuService sysMenuService;

    /**
     * 用户分页
     * @param req 查询信息
     * @return 用户分页
     */
    @GetMapping
    @SaCheckPermission("system:user:query")
    public IPage<SysUserPageResp> page(SysUserPageReq req){
        return sysUserService.page(req);
    }

    /**
     * 获取用户信息
     * @param id 用户编号
     * @return 用户信息
     */
    @GetMapping("/{id}")
    @SaCheckPermission("system:user:query")
    public SysUserInfoResp info(@NotNull(message = "{validation.user.id.NotNull}") @PathVariable Long id){
        return sysUserService.userInfo(id);
    }

    /**
     * 获取当前登录人的用户信息
     * @return 用户信息
     */
    @GetMapping("/me")
    public SysUserInfoResp currentUserInfo(){
        return sysUserService.userInfo(UserUtil.getUserId());
    }

    /**
     * 添加用户
     * @param req 用户信息
     * @return 用户编号
     */
    @PostMapping
    @SaCheckPermission("system:user:add")
    public Long add(@Valid @RequestBody SysUserAddReq req){
        return sysUserService.add(req);
    }

    /**
     * 编辑用户
     * @param id 用户编号
     * @param req 用户信息
     */
    @PutMapping("/{id}")
    @SaCheckPermission("system:user:edit")
    public void edit(@NotNull(message = "{validation.user.id.NotNull}") @PathVariable Long id, @Valid @RequestBody SysUserEditReq req){
        Long oldDeptId = sysUserService.lambdaQuery().select(SysUser::getId, SysUser::getDeptId).eq(SysUser::getId, id).one().getDeptId();
        sysUserService.edit(id, req);
        // 是否修改部门，如果修改，需要将目标用户踢出下线
        if (!Objects.equals(oldDeptId, req.getDeptId())){
            sysUserService.logout(String.valueOf(id));
        }
    }

    /**
     * 删除用户
     * @param id 用户编号
     */
    @DeleteMapping("/{id}")
    @SaCheckPermission("system:user:remove")
    public void remove(@NotNull(message = "{validation.user.id.NotNull}") @PathVariable Long id){
        sysUserService.remove(id);
    }

    /**
     * 重置密码
     * 重置成功后会登出
     * @param id 用户编号
     * @param newPassword 新密码
     */
    @PatchMapping("/{id}/password")
    public void resetPassword(@NotNull(message = "{validation.user.id.NotNull}") @PathVariable Long id, 
                               @NotNull(message = "{validation.user.password.NotBlank}") @RequestParam String newPassword){
        sysUserService.restPassword(id, newPassword);
        // 登出用户
        sysUserService.logout(String.valueOf(id));
    }

    /**
     * 更新用户状态
     * @param id 用户编号
     * @param status 状态值
     */
    @PatchMapping("/{id}/status")
    @SaCheckPermission("system:user:edit")
    public void updateStatus(@NotNull(message = "{validation.user.id.NotNull}") @PathVariable Long id,
                              @NotNull @RequestParam SysStatusEnum status){
        sysUserService.updateStatus(id, status);
    }

    /**
     * 获取当前用户的角色列表 （简易列表，主要用于下拉框）
     * @return 角色列表
     */
    @GetMapping("/me/roles")
    public List<SysRoleBaseListResp> currentUserRoles(){
        return sysRoleService.getRoleList(null);
    }

    /**
     * 获取指定用户角色列表 （简易列表，主要用于下拉框）
     * @param id 用户编号
     * @return 角色列表
     */
    @GetMapping("/{id}/roles")
    public List<SysRoleBaseListResp> userRoles(@PathVariable("id") Long id){
        return sysRoleService.getRoleList(id);
    }

    /**
     * 获取当前用户的菜单列表
     * @return 菜单列表
     */
    @GetMapping("/me/menus")
    public List<SysMenuListResp> currentUserMenus(){
        return sysMenuService.list(UserUtil.getUserId());
    }

}
