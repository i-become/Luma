package com.luma.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.luma.system.domain.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.luma.system.domain.vo.*;
import com.luma.system.enums.SysStatusEnum;

/**
* @author i-become
* @description 针对表【sys_user(用户信息表)】的数据库操作Service
* @createDate 2024-07-31 12:00:51
*/
public interface SysUserService extends IService<SysUser> {

    /**
     * 用户登录
     * @param req
     * @param loginIp
     * @return
     */
    SysUserLoginResp login(SysUserLoginReq req, String loginIp);

    /**
     * 单点注销
     */
    void logout(String userId);

    /**
     * 用户分页
     * @param req
     * @return
     */
    IPage<SysUserPageResp> page(SysUserPageReq req);

    /**
     * 用户信息
     * @param id 用户编号
     * @return
     */
    SysUserInfoResp userInfo(Long id);

    /**
     * 添加用户
     * @param req
     * @return
     */
    Long add(SysUserAddReq req);

    /**
     * 编辑用户
     * @param id 用户编号
     * @param req
     * @return
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
    void restPassword(Long id, String newPassword);

    /**
     * 修改用户状态
     * @param id 用户编号
     * @param status 状态
     */
    void updateStatus(Long id, SysStatusEnum status);
}
