package com.luma.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.luma.system.domain.vo.SysDeptAddReq;
import com.luma.system.domain.vo.SysDeptBaseListResp;
import com.luma.system.domain.vo.SysDeptListResp;
import com.luma.system.enums.SysStatusEnum;
import com.luma.system.service.SysDeptService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门相关接口
 * @author 刘靖
 */
@RestController
@RequestMapping("/sys/dept")
public class SysDeptController {

    @Resource
    private SysDeptService sysDeptService;

    /**
     * 部门列表
     * @param name 部门名称 （模糊搜索）
     * @param status 部门状态 不传为所有
     * @return
     */
    @GetMapping("/list")
    @SaCheckPermission("system:dept:query")
    public List<SysDeptListResp> list(String name, SysStatusEnum status){
        return sysDeptService.list(name, status);
    }

    /**
     * 获取部门列表
     * 如果传入角色编号，会返回部门有没有关联该角色
     * @param roleId 角色编号
     * @return
     */
    @GetMapping("/base_list")
    public List<SysDeptBaseListResp> baseList(Long roleId){
        return sysDeptService.baseList(roleId);
    }

    /**
     * 添加部门
     * @param req
     */
    @PostMapping
    @SaCheckPermission("system:dept:add")
    public void add(@Validated @RequestBody SysDeptAddReq req){
        sysDeptService.add(req);
    }

    /**
     * 编辑部门
     * @param id 部门编号
     * @param req
     */
    @PutMapping("/{id}")
    @SaCheckPermission("system:dept:edit")
    public void edit(@PathVariable Long id, @Validated @RequestBody SysDeptAddReq req){
        sysDeptService.edit(id, req);
    }

    /**
     * 删除部门
     * @param id 部门编号
     */
    @DeleteMapping
    @SaCheckPermission("system:dept:remove")
    public void remove(Long id){
        sysDeptService.remove(id);
    }

}
