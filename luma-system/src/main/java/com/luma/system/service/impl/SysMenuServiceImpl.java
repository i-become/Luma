package com.luma.system.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.luma.common.domain.SysUserRolePermission;
import com.luma.common.exception.system.ISystemException;
import com.luma.common.utils.MapstructUtil;
import com.luma.framework.permission.IStpInterface;
import com.luma.framework.utils.RedisUtil;
import com.luma.framework.utils.UserUtil;
import com.luma.system.domain.entity.SysMenu;
import com.luma.system.domain.entity.SysRoleMenu;
import com.luma.system.domain.vo.SysMenuAddReq;
import com.luma.system.domain.vo.SysMenuBaseListResp;
import com.luma.system.domain.vo.SysMenuListResp;
import com.luma.system.domain.vo.SysRoleBaseListResp;
import com.luma.system.enums.MenuTypeEnum;
import com.luma.system.mapper.SysMenuMapper;
import com.luma.system.mapper.SysRoleMapper;
import com.luma.system.mapper.SysRoleMenuMapper;
import com.luma.system.service.SysMenuService;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
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
    @CacheEvict(cacheNames = "luma:role:perms", key = "T(com.luma.framework.utils.UserUtil).getAdminRoleId()")
    public Long add(SysMenuAddReq req){
        // 校验菜单类型和权限标识
        validateMenuPermission(req.getType(), req.getPerms());
        
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
        // 新增菜单关联上超级管理员
        SysRoleMenu sysRoleMenu = new SysRoleMenu();
        sysRoleMenu.setMenuId(sysMenu.getId());
        sysRoleMenu.setRoleId(UserUtil.getAdminRoleId());
        sysRoleMenuMapper.insert(sysRoleMenu);
        return sysMenu.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(Long id, SysMenuAddReq req){
        // 校验菜单类型和权限标识
        validateMenuPermission(req.getType(), req.getPerms());
        
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
        // 清除权限缓存
        removeRolePermsByMenuIds(Collections.singletonList(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(Long id){
        // 获取子孙节点id
        List<Long> allIds = new ArrayList<>();
        allIds.add(id);
        List<Long> ids = new ArrayList<>();
        ids.add(id);
        while (!ids.isEmpty()){
            ids = getChildIdList(ids);
            allIds.addAll(ids);
        }
        baseMapper.deleteByIds(allIds);
        // 清除权限缓存
        removeRolePermsByMenuIds(allIds);
    }

    /**
     * 获取子级菜单列表
     * @param ids 本级菜单id列表
     * @return 子级菜单列表
     */
    private List<Long> getChildIdList(List<Long> ids){
        return lambdaQuery().select(SysMenu::getId)
                .in(SysMenu::getParentId, ids)
                .list()
                .stream()
                .map(SysMenu::getId)
                .toList();
    }

    /**
     * 清除所有与该菜单关联的权限缓存
     * @param ids 菜单编号
     */
    private void removeRolePermsByMenuIds(List<Long> ids){
        // 清除所有与该菜单关联的权限缓存
        List<Long> roleIds = ChainWrappers.lambdaQueryChain(sysRoleMenuMapper)
                .select(SysRoleMenu::getRoleId)
                .in(SysRoleMenu::getMenuId, ids)
                .list()
                .stream()
                .map(SysRoleMenu::getRoleId)
                .distinct()
                .toList();
        for (Long roleId: roleIds) {
            RedisUtil.deleteObject("luma:role:perms:" + roleId);
        }
    }

    /**
     * 校验菜单类型和权限标识
     * @param type 菜单类型
     * @param perms 权限标识
     */
    private void validateMenuPermission(MenuTypeEnum type, String perms) {
        // 按钮类型必须有权限标识
        if (type == MenuTypeEnum.BUTTON && (perms == null || perms.trim().isEmpty())) {
            throw new IllegalArgumentException("按钮类型的菜单必须设置权限标识");
        }
        
        // 外链类型不应该有权限标识
        if (type == MenuTypeEnum.LINK && perms != null && !perms.trim().isEmpty()) {
            throw new IllegalArgumentException("外链类型的菜单不应该设置权限标识");
        }
    }

}
