package com.luma.system.service;

import com.luma.system.domain.entity.SysDept;
import com.baomidou.mybatisplus.extension.service.IService;
import com.luma.system.domain.vo.SysDeptAddReq;
import com.luma.system.domain.vo.SysDeptBaseListResp;
import com.luma.system.domain.vo.SysDeptListResp;
import com.luma.system.enums.SysStatusEnum;

import java.util.List;

/**
* @author i-become
* @description 针对表【sys_dept(部门表)】的数据库操作Service
* @createDate 2024-08-01 15:51:52
*/
public interface SysDeptService extends IService<SysDept> {

    /**
     * 部门列表
     * @param name 部门名称
     * @param status 状态
     * @return
     */
    List<SysDeptListResp> list(String name, SysStatusEnum status);

    /**
     * 获取部门列表，如果传入角色编号，会返回部门有没有关联该角色
     * @param roleId 角色编号
     * @return
     */
    List<SysDeptBaseListResp> baseList(Long roleId);

    /**
     * 添加部门
     * @param req
     */
    void add(SysDeptAddReq req);

    /**
     * 修改部门
     * @param id 部门编号
     * @param req
     */
    void edit(Long id, SysDeptAddReq req);

    /**
     * 删除部门
     * @param id 部门编号
     */
    void remove(Long id);
}
