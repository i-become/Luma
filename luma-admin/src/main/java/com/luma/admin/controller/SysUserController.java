package com.luma.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.luma.framework.utils.UserUtil;
import com.luma.system.domain.entity.SysUser;
import com.luma.system.domain.vo.*;
import com.luma.system.enums.SysStatusEnum;
import com.luma.system.service.SysUserService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * 系统用户相关接口
 * @author i-become
 */
@Validated
@RestController
@RequestMapping("/sys/user")
public class SysUserController {

    @Resource
    private SysUserService sysUserService;

    /**
     * 用户分页
     * @param req 查询信息
     * @return 用户分页
     */
    @GetMapping("/page")
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
    public SysUserInfoResp userinfo(@NotNull(message = "{validation.user.id.NotNull}") @PathVariable Long id){
        return sysUserService.userInfo(id);
    }

    /**
     * 获取当前登录人的用户信息
     * @return 用户信息
     */
    @GetMapping("/self")
    public SysUserInfoResp selfInfo(){
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
    @PostMapping("/{id}/rest_password")
    public void restPassword(@NotNull(message = "{validation.user.id.NotNull}") @PathVariable Long id, @NotNull(message = "{validation.user.password.NotBlank}") String newPassword){
        sysUserService.restPassword(id, newPassword);
        // 登出用户
        sysUserService.logout(String.valueOf(id));
    }

    /**
     * 启用用户
     * @param id 用户编号
     */
    @PostMapping("/enable/{id}")
    @SaCheckPermission("system:user:edit")
    public void enable(@NotNull(message = "{validation.user.id.NotNull}") @PathVariable Long id){
        sysUserService.updateStatus(id, SysStatusEnum.NORMAL);
    }

    /**
     * 禁用用户
     * @param id 用户编号
     */
    @PostMapping("/disable/{id}")
    @SaCheckPermission("system:user:edit")
    public void disable(@NotNull(message = "{validation.user.id.NotNull}") @PathVariable Long id){
        sysUserService.updateStatus(id, SysStatusEnum.DISABLED);
    }

}
