package com.luma.framework.permission;

import cn.dev33.satoken.stp.StpInterface;
import com.luma.common.domain.SysUserAuthInfo;

/**
 * @author i-become
 */
public interface IStpInterface extends StpInterface {

    /**
     * 获取用户权限信息
     * @param loginId
     * @return
     */
    SysUserAuthInfo getUserAuthInfo(Object loginId);
}
