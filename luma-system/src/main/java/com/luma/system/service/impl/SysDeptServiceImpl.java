package com.luma.system.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luma.common.annotation.DataScope;
import com.luma.common.domain.BaseEntity;
import com.luma.common.exception.system.ISystemException;
import com.luma.common.utils.MapstructUtil;
import com.luma.system.domain.entity.SysDept;
import com.luma.system.domain.vo.SysDeptAddReq;
import com.luma.system.domain.vo.SysDeptBaseListResp;
import com.luma.system.domain.vo.SysDeptListResp;
import com.luma.system.domain.vo.SysRoleBaseListResp;
import com.luma.system.enums.SysStatusEnum;
import com.luma.system.mapper.SysRoleMapper;
import com.luma.system.service.SysDeptService;
import com.luma.system.mapper.SysDeptMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author i-become
* @description 针对表【sys_dept(部门表)】的数据库操作Service实现
* @createDate 2024-08-01 15:51:52
*/
@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept>
    implements SysDeptService{

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Override
    @DataScope(deptIdColumnName = "id")
    @Transactional(readOnly = true)
    public List<SysDeptListResp> list(String name, SysStatusEnum status){
        List<SysDept> deptList = lambdaQuery().select(SysDept::getId, SysDept::getParentId, SysDept::getDeptName, SysDept::getStatus,
                        SysDept::getEmail, SysDept::getPhone, SysDept::getLeader ,SysDept::getSort, BaseEntity::getCreateTime)
                .like(StringUtils.isNotBlank(name), SysDept::getDeptName, name)
                .eq(status != null, SysDept::getStatus, status)
                .list();
        return MapstructUtil.convert(deptList, SysDeptListResp.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SysDeptBaseListResp> getBaseListByRoleId(Long roleId){
        // 判断是否有该角色权限
        if (!sysRoleMapper.selectRoleList(null).stream().map(SysRoleBaseListResp::getId).toList().contains(roleId)) {
            throw new ISystemException("exception.dept.noRolePermission");
        }
        return baseMapper.selectBaseListByRoleId(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(SysDeptAddReq req){
        // 获取上级信息，判断是否有权限对其进行新增
        SysDept parentDept = lambdaQuery().select(SysDept::getId, SysDept::getAncestors).eq(SysDept::getId, req.getParentId()).one();
        if (!lambdaQuery().eq(SysDept::getId, req.getParentId()).exists()){
            throw new ISystemException("exception.dept.parent.notFound");
        }
        if (!baseMapper.selectIdList().contains(req.getParentId())){
            throw new ISystemException("exception.dept.noParentPermission");
        }
        // 保存部门数据
        SysDept sysDept = MapstructUtil.convert(req, SysDept.class);
        baseMapper.insert(sysDept);
        // 更新祖籍编号
        sysDept.setAncestors(parentDept.getAncestors() + sysDept.getId() + ",");
        baseMapper.updateById(sysDept);
        return sysDept.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(Long id, SysDeptAddReq req){
        SysDept sysDept = lambdaQuery().select(SysDept::getId, SysDept::getParentId).eq(SysDept::getId, id).one();
        if (sysDept == null){
            throw new ISystemException("exception.dept.noPermission");
        }
        // 是否有该部门权限
        List<Long> deptIds = baseMapper.selectIdList();
        if (!deptIds.contains(id)){
            throw new ISystemException("exception.dept.noPermission");
        }
        // 判断是否需要修改上级部门编号
        if (!sysDept.getParentId().equals(req.getParentId())){
            // 获取上级信息，判断是否有权限对其进行新增
            SysDept parentDept = lambdaQuery().select(SysDept::getId, SysDept::getAncestors).eq(SysDept::getId, req.getParentId()).one();
            if (!lambdaQuery().eq(SysDept::getId, req.getParentId()).exists()){
                throw new ISystemException("exception.dept.parent.notFound");
            }
            if (!deptIds.contains(req.getParentId())){
                throw new ISystemException("exception.dept.noParentPermission");
            }
            // 上级部门发生变动，需要将本级以及本级下所有子孙节点的祖籍进行修改
            SysDept oldParentDept = lambdaQuery().select(SysDept::getId, SysDept::getAncestors).eq(SysDept::getId, sysDept.getParentId()).one();
            baseMapper.updateDeptAncestors(id, parentDept.getAncestors(), oldParentDept.getAncestors().length());
        }
        // 保存修改信息
        SysDept dept = MapstructUtil.convert(req, sysDept);
        dept.setId(id);
        baseMapper.updateById(dept);
    }

    @Override
    public void remove(Long id){
        // 是否有该部门权限
        List<Long> deptIds = baseMapper.selectIdList();
        if (!deptIds.contains(id)){
            throw new ISystemException("exception.dept.noPermission");
        }
        SysDept sysDept = lambdaQuery().select(SysDept::getId, SysDept::getAncestors).eq(SysDept::getId, id).one();
        if (sysDept == null){
            throw new ISystemException("exception.dept.notFound");
        }
        // 删除该部门以及子孙节点
        lambdaUpdate().likeRight(SysDept::getAncestors, sysDept.getAncestors()).or().eq(SysDept::getId, id).remove();
    }

}




