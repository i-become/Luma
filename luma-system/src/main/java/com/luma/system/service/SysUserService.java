package com.luma.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.luma.system.domain.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.luma.system.domain.vo.*;
import com.luma.system.enums.SysStatusEnum;

/**
* @author i-become
* 针对表【sys_user(用户信息表)】的数据库操作Service
*/
public interface SysUserService extends IService<SysUser> {

    /**
     * 用户登录
     * @param req 登录信息
     * @param loginIp 登录编号
     * @return 登录结果
     */
    SysUserLoginResp login(SysUserLoginReq req, String loginIp);

    /**
     * 单点注销
     */
    void logout(String userId);

    /**
     * 用户分页
     * @param req 查询信息
     * @return 用户分页
     */
    IPage<SysUserPageResp> page(SysUserPageReq req);

    /**
     * 用户信息
     * @param id 用户编号
     * @return 用户信息
     */
    SysUserInfoResp userInfo(Long id);

    /**
     * 用户是否被禁用
     * @param userId 用户编号
     * @return 是否成功
     */
    Boolean isDisable(Long userId);

    /**
     * 添加用户
     * @param req 用户信息
     * @return 用户编号
     */
    Long add(SysUserAddReq req);

    /**
     * 编辑用户
     * @param id 用户编号
     * @param req 用户信息
     */
    void edit(Long id, SysUserEditReq req);

    /**
     * 删除用户
     * @param id 用户编号
     */
    void remove(Long id);

    /**
     * 重置密码
     * @param id 用户编号
     * @param newPassword 新密码
     */
    void resetPassword(Long id, String newPassword);

    /**
     * 修改用户状态
     * @param id 用户编号
     * @param status 状态
     */
    void updateStatus(Long id, SysStatusEnum status);
}
