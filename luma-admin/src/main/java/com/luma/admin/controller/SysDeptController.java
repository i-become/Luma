package com.luma.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.luma.system.domain.vo.SysDeptAddReq;
import com.luma.system.domain.vo.SysDeptBaseListResp;
import com.luma.system.domain.vo.SysDeptListResp;
import com.luma.system.enums.SysStatusEnum;
import com.luma.system.service.SysDeptService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门相关接口
 * @author i-become
 */
@Validated
@RestController
@RequestMapping("/dept")
public class SysDeptController {

    @Resource
    private SysDeptService sysDeptService;

    /**
     * 部门列表
     * @param name 部门名称 （模糊搜索）
     * @param status 部门状态 不传为所有
     * @return 部门列表
     */
    @GetMapping
    @SaCheckPermission("system:dept:query")
    public List<SysDeptListResp> list(String name, SysStatusEnum status){
        return sysDeptService.list(name, status);
    }

    /**
     * 获取部门列表
     * 如果传入角色编号，会返回部门有没有关联该角色
     * @param roleId 角色编号
     * @return 部门列表
     */
    @GetMapping("/list")
    public List<SysDeptBaseListResp> baseList(Long roleId){
        return sysDeptService.baseList(roleId);
    }

    /**
     * 添加部门
     * @param req 部门信息
     * @return 部门编号
     */
    @PostMapping
    @SaCheckPermission("system:dept:add")
    public Long add(@Valid @RequestBody SysDeptAddReq req){
        return sysDeptService.add(req);
    }

    /**
     * 编辑部门
     * @param id 部门编号
     * @param req 部门信息
     */
    @PutMapping("/{id}")
    @SaCheckPermission("system:dept:edit")
    public void edit(@NotNull(message = "{validation.dept.id.NotNull}") @PathVariable Long id, @Valid @RequestBody SysDeptAddReq req){
        sysDeptService.edit(id, req);
    }

    /**
     * 删除部门
     * @param id 部门编号
     */
    @DeleteMapping("/{id}")
    @SaCheckPermission("system:dept:remove")
    public void remove(@NotNull(message = "{validation.dept.id.NotNull}") @PathVariable Long id){
        sysDeptService.remove(id);
    }

}
