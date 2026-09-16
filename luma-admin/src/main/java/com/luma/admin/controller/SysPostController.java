package com.luma.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.luma.system.domain.vo.SysPostAddReq;
import com.luma.system.domain.vo.SysPostBaseListResp;
import com.luma.system.domain.vo.SysPostPageReq;
import com.luma.system.domain.vo.SysPostPageResp;
import com.luma.system.enums.SysStatusEnum;
import com.luma.system.service.SysPostService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统岗位相关接口
 * @author i-become
 */
@Validated
@RestController
@RequestMapping("/post")
public class SysPostController {

    @Resource
    private SysPostService sysPostService;

    /**
     * 岗位分页
     * @param req 查询信息
     * @return 岗位分页
     */
    @GetMapping
    @SaCheckPermission("system:post:query")
    public IPage<SysPostPageResp> page(SysPostPageReq req){
        return sysPostService.page(req);
    }

    /**
     * 岗位基础列表
     * @return 岗位列表
     */
    @GetMapping("/base-list")
    public List<SysPostBaseListResp> baseList(){
        return sysPostService.getBaseList();
    }

    /**
     * 添加岗位
     * @param req 岗位信息
     * @return 岗位编号
     */
    @PostMapping
    @SaCheckPermission("system:post:add")
    public Long add(@Valid @RequestBody SysPostAddReq req){
        return sysPostService.add(req);
    }

    /**
     * 编辑岗位
     * @param id 岗位编号
     * @param req 岗位信息
     */
    @PutMapping("/{id}")
    @SaCheckPermission("system:post:edit")
    public void edit(@NotNull(message = "{validation.post.id.NotNull}") @PathVariable Long id, @Valid @RequestBody SysPostAddReq req){
        sysPostService.edit(id, req);
    }

    /**
     * 删除岗位
     * @param id 岗位编号
     */
    @DeleteMapping("/{id}")
    @SaCheckPermission("system:post:remove")
    public void remove(@NotNull(message = "{validation.post.id.NotNull}") @PathVariable Long id){
        sysPostService.remove(id);
    }

    /**
     * 更新岗位状态
     * @param id 岗位编号
     * @param status 状态值
     */
    @PatchMapping("/{id}/status")
    @SaCheckPermission("system:post:edit")
    public void updateStatus(@NotNull(message = "{validation.post.id.NotNull}") @PathVariable Long id,
                             @NotNull @RequestParam SysStatusEnum status){
        sysPostService.updateStatus(id, status);
    }

}
