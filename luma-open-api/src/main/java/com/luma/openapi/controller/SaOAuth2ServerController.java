package com.luma.openapi.controller;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.config.SaOAuth2ServerConfig;
import cn.dev33.satoken.oauth2.processor.SaOAuth2ServerProcessor;
import cn.dev33.satoken.oauth2.template.SaOAuth2Template;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaFoxUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.luma.common.domain.R;
import com.luma.common.exception.system.ISystemException;
import com.luma.common.utils.ServletUtil;
import com.luma.framework.permission.TenantContextHolder;
import com.luma.system.domain.entity.Client;
import com.luma.system.domain.entity.SysTenant;
import com.luma.system.domain.vo.SysUserLoginReq;
import com.luma.system.domain.vo.SysUserLoginResp;
import com.luma.system.service.ClientService;
import com.luma.system.service.SysTenantService;
import com.luma.system.service.SysUserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

/**
 * Sa-OAuth2 Server端 控制器
 */
@Slf4j
@RestController
public class SaOAuth2ServerController {

    @Resource
    private SysUserService sysUserService;

    @Resource
    private ClientService clientService;

    @Resource
    private SysTenantService sysTenantService;

    @Resource
    private SpringTemplateEngine templateEngine;

    /**
     * 处理所有OAuth相关请求
     * @return
     */
    @RequestMapping("/oauth2/*")
    public Object request(HttpServletRequest request) {
        log.info("------- 进入请求: " + SaHolder.getRequest().getUrl());
        // doConfirm 确认授权接口
        SaRequest req = SaHolder.getRequest();
        if (req.isPath("/oauth2/doLogout")){
            sysUserService.logout(StpUtil.getLoginIdAsString());
            return R.success();
        }
        else {
            return SaOAuth2ServerProcessor.instance.dister();
        }
    }

    /**
     * Sa-OAuth2 定制化配置
     * @param cfg
     */
    @Autowired
    public void setSaOAuth2Config(SaOAuth2ServerConfig cfg) {
        // 配置：未登录时返回的View
        cfg.notLoginView = () -> {
            Context context = new Context();
            context.setVariable("nginxPrefix", SaHolder.getRequest().getHeader("X-Forwarded-Prefix"));
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .header("Content-Type", "text/html")
                    .body(templateEngine.process("login", context));
        };

        // 配置：登录处理函数
        cfg.doLoginHandle = (name, pwd) -> {
            SysUserLoginReq req = new SysUserLoginReq();
            String tenantAlias = SaHolder.getRequest().getParam("tenantAlias");
            if (StringUtils.isBlank(tenantAlias)){
                req.setTenantId(TenantContextHolder.SYS_TENANT_ID);
            }else {
                SysTenant tenant = sysTenantService.lambdaQuery().select(SysTenant::getId).eq(SysTenant::getAlias, tenantAlias).one();
                if (tenant == null){
                    throw new ISystemException("账号信息错误");
                }
                req.setTenantId(tenant.getId());
            }

            req.setLoginName(name);
            req.setPassword(pwd);
            req.setCode(SaHolder.getRequest().getParam("code"));
            SysUserLoginResp resp = sysUserService.login(req, ServletUtil.getClientIP(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest()));
            // 关联
            return resp;
        };

        // 配置：确认授权时返回的View
        cfg.confirmView = (clientId, scopes) -> {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            // 判断是否为静默授权，如果为静默授权则直接跳过授权
            if (clientService.lambdaQuery().select(Client::getId, Client::getIsConfirm).eq(Client::getId, clientId).one().getIsConfirm()){
                // 授权
                SaOAuth2Template oauth2Template = SaOAuth2Manager.getTemplate();
                Object loginId = SaOAuth2Manager.getStpLogic().getLoginId();
                oauth2Template.saveGrantScope(clientId, loginId, scopes);
                String proto = request.getHeader("X-Forwarded-Proto");
                proto = proto == null ? "http://" : proto + "://";
                String port = request.getHeader("X-Forwarded-Port");
                port = port == null ? "80" : port;
                String prefix = request.getHeader("X-Forwarded-Prefix");
                prefix = prefix == null ? "" : prefix;
                // 重定向到目标地址
                String redirect = proto +
                        request.getHeader("Host") + ":" +
                        port +
                        prefix +
                        request.getRequestURI() + "?" +
                        request.getQueryString();
                return SaHolder.getResponse().redirect(redirect);
            }

            // satoken bug 授权通过仍然会进这里

            String scopeStr = SaFoxUtil.convertListToString(scopes);
//            String yesCode =
//                    "fetch('/oauth2/doConfirm?client_id=" + clientId + "&scope=" + scopeStr + "&build_redirect_uri=true" + "', {method: 'POST'})" +
//                            ".then(res => res.json())" +
//                            ".then(res => location.reload())";
            String yesCode =
                    "fetch('/openapi/v1/oauth2/doConfirm?" + request.getQueryString() + "&build_redirect_uri=true" + "', {method: 'POST'})" +
                            ".then(res => res.json())";
            String res = "<p>应用 " + clientId + " 请求授权：" + scopeStr + "，是否同意？</p>"
                    + "<p>" +
                    "        <button onclick=\"" + yesCode + "\">同意</button>" +
                    "        <button onclick='history.back()'>拒绝</button>" +
                    "</p>";
            return res;
        };
    }

}
