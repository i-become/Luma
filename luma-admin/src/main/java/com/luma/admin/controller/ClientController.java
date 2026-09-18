package com.luma.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.luma.system.domain.vo.*;
import com.luma.system.service.ClientService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 第三方应用（OAuth2 Client）相关接口
 * @author i-become
 */
@Validated
@RestController
@RequestMapping("/client")
public class ClientController {

    @Resource
    private ClientService clientService;

    /**
     * 应用分页
     * @param req 查询信息
     * @return 应用分页
     */
    @GetMapping
    @SaCheckPermission("system:client:query")
    public IPage<ClientPageResp> page(ClientPageReq req){
        return clientService.page(req);
    }

    /**
     * 应用基础列表
     * @return 应用列表
     */
    @GetMapping("/base-list")
    public List<ClientBaseListResp> baseList(){
        return clientService.getBaseList();
    }

    /**
     * 获取应用详情
     * @param id 应用编号
     * @return 应用详情
     */
    @GetMapping("/{id}")
    @SaCheckPermission("system:client:query")
    public ClientInfoResp info(@NotNull(message = "{validation.client.id.NotNull}") @PathVariable Long id){
        return clientService.info(id);
    }

    /**
     * 添加应用
     * @param req 应用信息
     * @return 应用编号
     */
    @PostMapping
    @SaCheckPermission("system:client:add")
    public Long add(@Valid @RequestBody ClientAddReq req){
        return clientService.add(req);
    }

    /**
     * 编辑应用
     * @param id 应用编号
     * @param req 应用信息
     */
    @PutMapping("/{id}")
    @SaCheckPermission("system:client:edit")
    public void edit(@NotNull(message = "{validation.client.id.NotNull}") @PathVariable Long id, @Valid @RequestBody ClientAddReq req){
        clientService.edit(id, req);
    }

    /**
     * 删除应用
     * @param id 应用编号
     */
    @DeleteMapping("/{id}")
    @SaCheckPermission("system:client:remove")
    public void remove(@NotNull(message = "{validation.client.id.NotNull}") @PathVariable Long id){
        clientService.remove(id);
    }

    /**
     * 重置应用密钥
     * @param id 应用编号
     * @return 新密钥
     */
    @PatchMapping("/{id}/secret")
    @SaCheckPermission("system:client:edit")
    public ClientSecretResp resetSecret(@NotNull(message = "{validation.client.id.NotNull}") @PathVariable Long id){
        return clientService.resetSecret(id);
    }

}
