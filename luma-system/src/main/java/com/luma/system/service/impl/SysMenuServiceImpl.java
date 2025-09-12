package com.luma.system.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luma.common.domain.SysUserRolePermission;
import com.luma.common.exception.system.ISystemException;
import com.luma.common.utils.MapstructUtil;
import com.luma.framework.permission.IStpInterface;
import com.luma.framework.utils.UserUtil;
import com.luma.system.domain.entity.SysMenu;
import com.luma.system.domain.vo.SysMenuAddReq;
import com.luma.system.domain.vo.SysMenuBaseListResp;
import com.luma.system.domain.vo.SysMenuListResp;
import com.luma.system.domain.vo.SysRoleBaseListResp;
import com.luma.system.mapper.SysMenuMapper;
import com.luma.system.mapper.SysRoleMapper;
import com.luma.system.mapper.SysRoleMenuMapper;
import com.luma.system.service.SysMenuService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author i-become
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    /**
     * 顶级id
     */
    public static final Long BASE_ID = 0L;

    @Resource
    private IStpInterface stpInterface;

    @Resource
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Override
    @Transactional(readOnly = true)
    public List<SysMenuListResp> list(Long userId){
        // 是否只获取自己关联的菜单，如果不是会获取自己有权限的菜单，这两个并不等同，自己关联的菜单是自己拥有角色关联的菜单集合，而自己有权限的菜单是指对菜单数据本身的操作权限，根据自己角色最大的权限范围决定
        List<Long> roleIds = stpInterface.getRolePermissionList(userId).stream().map(SysUserRolePermission::getId).toList();
        if (roleIds.isEmpty()){
            return new ArrayList<>();
        }
        return sysRoleMenuMapper.selectMenuListByRoleIds(roleIds);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SysMenuBaseListResp> getBaseListByRoleId(Long roleId){
        // 判断是否有该角色权限
        if (!sysRoleMapper.selectRoleList(null).stream().map(SysRoleBaseListResp::getId).toList().contains(roleId)) {
            throw new ISystemException("没有该角色权限");
        }
        return baseMapper.selectBaseListByRoleId(roleId);
    }

    @Override
    public Long add(SysMenuAddReq req){
        // 如果上级存在且不是0，那么获取上级信息，判断是否存在
        if (req.getParentId() != null && !req.getParentId().equals(BASE_ID)){
            if (!lambdaQuery().eq(SysMenu::getId, req.getParentId()).exists()){
                throw new IllegalArgumentException("parentId 错误，不存在该菜单");
            }
        }else {
            req.setParentId(BASE_ID);
        }
        // 入库
        SysMenu sysMenu = MapstructUtil.convert(req, SysMenu.class);
        sysMenu.setId(IdUtil.getSnowflakeNextId());
        this.save(sysMenu);
        return sysMenu.getId();
    }

    @Override
    public void edit(Long id, SysMenuAddReq req){
        // 获取原有菜单，判断是否存在
        SysMenu sysMenu = lambdaQuery().select(SysMenu::getId, SysMenu::getParentId).eq(SysMenu::getId, id).one();
        if (sysMenu == null){
            throw new IllegalArgumentException("id 错误，不存在该菜单");
        }
        // 判断上级编号是否变化，如果变化，需要重新判断上级是否存在
        if (req.getParentId() != null && !req.getParentId().equals(sysMenu.getParentId()) && !req.getParentId().equals(BASE_ID)){
            if (!lambdaQuery().eq(SysMenu::getId, req.getParentId()).exists()){
                throw new IllegalArgumentException("parentId 错误，不存在该菜单");
            }
        }
        SysMenu newMenu = MapstructUtil.convert(req, SysMenu.class);
        newMenu.setId(id);
        this.updateById(newMenu);
    }

}
