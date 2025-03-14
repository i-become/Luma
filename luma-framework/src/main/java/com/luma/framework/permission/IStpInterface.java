package com.luma.framework.permission;

import cn.dev33.satoken.stp.StpInterface;
import com.luma.common.domain.SysUserAuthInfo;

/**
 * @author 刘靖
 */
public interface IStpInterface extends StpInterface {

    /**
     * 获取用户权限信息
     * @param loginId
     * @return
     */
    SysUserAuthInfo getUserAuthInfo(Object loginId);
}
