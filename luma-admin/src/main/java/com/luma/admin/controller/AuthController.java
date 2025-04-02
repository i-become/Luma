package com.luma.admin.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.luma.common.exception.system.ISystemException;
import com.luma.common.utils.ServletUtil;
import com.luma.framework.permission.TenantContextHolder;
import com.luma.system.domain.entity.SysTenant;
import com.luma.system.domain.vo.SysUserLoginReq;
import com.luma.system.domain.vo.SysUserLoginResp;
import com.luma.system.service.SysTenantService;
import com.luma.system.service.SysUserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 鉴权相关接口
 */
@RestController
public class AuthController {

    @Resource
    private SysUserService sysUserService;

    @Resource
    private SysTenantService sysTenantService;

    /**
     * 用户登录
     * @param request
     * @param req
     * @return
     */
    @PostMapping("/login")
    public SysUserLoginResp login(HttpServletRequest request, @Valid SysUserLoginReq req){
        if (StringUtils.isBlank(req.getTenantAlias())){
            req.setTenantId(TenantContextHolder.SYS_TENANT_ID);
        }else {
            SysTenant tenant = sysTenantService.lambdaQuery().select(SysTenant::getId).eq(SysTenant::getAlias, req.getTenantAlias()).one();
            if (tenant == null){
                throw new ISystemException("账号信息错误");
            }
            req.setTenantId(tenant.getId());
        }
        return sysUserService.login(req, ServletUtil.getClientIP(request));
    }

    /**
     * 用户登出
     */
    @GetMapping("/logout")
    public void logout(){
        sysUserService.logout(StpUtil.getLoginIdAsString());
    }

}
