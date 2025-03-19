package com.luma.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.luma.framework.utils.UserUtil;
import com.luma.system.domain.entity.SysUser;
import com.luma.system.domain.vo.*;
import com.luma.system.enums.SysStatusEnum;
import com.luma.system.service.SysUserService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * 系统用户相关接口
 * @author i-become
 */
@RequestMapping("/sys/user")
@RestController
public class SysUserController {

    @Resource
    private SysUserService sysUserService;

    /**
     * 用户分页
     * @param req
     * @return
     */
    @GetMapping("/page")
    @SaCheckPermission("system:user:query")
    public IPage<SysUserPageResp> page(SysUserPageReq req){
        return sysUserService.page(req);
    }

    /**
     * 获取用户信息
     * @param id 用户编号
     * @return
     */
    @GetMapping
    @SaCheckPermission("system:user:query")
    public SysUserInfoResp userinfo(Long id){
        return sysUserService.userInfo(id);
    }

    /**
     * 获取当前登录人的用户信息
     * @return
     */
    @GetMapping("/self_info")
    public SysUserInfoResp selfInfo(){
        return sysUserService.userInfo(UserUtil.getUserId());
    }

    /**
     * 添加用户
     * @param req
     * @return
     */
    @PostMapping
    @SaCheckPermission("system:user:add")
    public Long add(@Validated @RequestBody SysUserAddReq req){
        return sysUserService.add(req);
    }

    /**
     * 编辑用户
     * @param id 用户编号
     * @param req
     */
    @PutMapping("/{id}")
    @SaCheckPermission("system:user:edit")
    public void edit(@PathVariable Long id, @Validated @RequestBody SysUserEditReq req){
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
    @DeleteMapping
    @SaCheckPermission("system:user:remove")
    public void remove(Long id){
        sysUserService.remove(id);
    }

    /**
     * 重置密码
     * 重置成功后会登出
     * @param id 用户编号
     * @param newPassword 新密码
     */
    @PostMapping("/rest_password")
    public void restPassword(Long id, @NotNull(message = "密码不能为空") String newPassword){
        sysUserService.restPassword(id, newPassword);
        // 登出用户
        sysUserService.logout(String.valueOf(id));
    }

    /**
     * 启用用户
     * @param id 用户编号
     */
    @PostMapping("/enable")
    @SaCheckPermission("system:user:edit")
    public void enable(Long id){
        sysUserService.updateStatus(id, SysStatusEnum.NORMAL);
    }

    /**
     * 禁用用户
     * @param id 用户编号
     */
    @PostMapping("/disable")
    @SaCheckPermission("system:user:edit")
    public void disable(Long id){
        sysUserService.updateStatus(id, SysStatusEnum.DISABLED);
    }

}
