package com.luma.system.service.impl;

import cn.hutool.core.comparator.CompareUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.luma.common.annotation.DataScope;
import com.luma.common.domain.SysRoleDataScopeInfo;
import com.luma.common.domain.SysUserRolePermission;
import com.luma.common.enums.DataScopeEnum;
import com.luma.common.exception.system.ISystemException;
import com.luma.common.utils.MapstructUtil;
import com.luma.framework.permission.IStpInterface;
import com.luma.framework.utils.UserUtil;
import com.luma.system.domain.entity.SysRole;
import com.luma.system.domain.entity.SysRoleMenu;
import com.luma.system.domain.entity.SysUserRole;
import com.luma.system.domain.vo.*;
import com.luma.system.enums.SysStatusEnum;
import com.luma.system.mapper.SysDeptMapper;
import com.luma.system.mapper.SysRoleMenuMapper;
import com.luma.system.mapper.SysUserRoleMapper;
import com.luma.system.service.SysRoleService;
import com.luma.system.mapper.SysRoleMapper;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 针对表【sys_role(角色信息表)】的数据库操作Service实现
 * @author i-become
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole>
    implements SysRoleService{

    @Resource
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Resource
    private SysUserRoleMapper sysUserRoleMapper;

    @Resource
    private SysDeptMapper sysDeptMapper;

    @Resource
    private IStpInterface stpInterface;

    @Override
    @Cacheable(cacheNames = "luma:role:perms", key = "#id", unless = "#result == null")
    public List<String> getRolePermissionListByRoleId(Long id){
        return sysRoleMenuMapper.selectRolePermissionListByRoleId(id);
    }

    @Override
    @Cacheable(cacheNames = "luma:user:roles", key = "#userId", unless = "#result == null")
    public List<SysRoleDataScopeInfo> getRoleDataScopeInfoListByUserId(Long userId){
        return sysUserRoleMapper.selectRoleDataScopeInfoListByUserId(userId);
    }

    @Override
    @DataScope(deptAlias = "su")
    public IPage<SysRolePageResp> page(SysRolePageReq req){
        return baseMapper.selectRolePage(req.toMpPage(), req);
    }

    @Override
    public List<SysRoleBaseListResp> getRoleList(Long userId){
        return baseMapper.selectRoleList(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(SysRoleAddReq req){
        // 判断权限字符是否重复
        if (lambdaQuery().eq(SysRole::getRoleKey, req.getRoleKey()).exists()){
            throw new ISystemException("角色权限标识已经存在");
        }
        // 校验当前用户是否有权限添加该角色
        check(req);

        // 保存角色
        SysRole sysRole = MapstructUtil.convert(req, SysRole.class);
        sysRole.setId(IdUtil.getSnowflakeNextId());
        baseMapper.insert(sysRole);
        // 建立当前用户与当前角色的关系
        SysUserRole sysUserRole = new SysUserRole();
        sysUserRole.setRoleId(sysRole.getId());
        sysUserRole.setUserId(UserUtil.getUserId());
        sysUserRoleMapper.insert(sysUserRole);
        // 保存角色和菜单的关系
        if (req.getMenuIdList() == null){
            return sysRole.getId();
        }
        List<SysRoleMenu> roleMenuList = new ArrayList<>();
        for (Long menuId : req.getMenuIdList()){
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setRoleId(sysRole.getId());
            roleMenu.setMenuId(menuId);
        }
        sysRoleMenuMapper.insert(roleMenuList);
        return sysRole.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(Long id, SysRoleAddReq req){
        // 对原有的角色进行判断
        if (!lambdaQuery().eq(SysRole::getId, id).exists()){
            throw new IllegalArgumentException("角色不存在");
        }
        // 判断权限字符是否重复
        if (lambdaQuery().eq(SysRole::getRoleKey, req.getRoleKey()).ne(SysRole::getId, id).exists()){
            throw new ISystemException("角色权限标识已经存在");
        }

        // 校验当前用户是否有权限编辑当前角色
        check(req);

        // 更新角色
        SysRole sysRole = MapstructUtil.convert(req, SysRole.class);
        sysRole.setId(id);
        baseMapper.updateById(sysRole);
        // 更新角色和菜单关系
        ChainWrappers.lambdaUpdateChain(sysRoleMenuMapper).eq(SysRoleMenu::getRoleId, id).remove();
        if (req.getMenuIdList() == null){
            return;
        }
        List<SysRoleMenu> roleMenuList = new ArrayList<>();
        for (Long menuId : req.getMenuIdList()){
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setRoleId(sysRole.getId());
            roleMenu.setMenuId(menuId);
            roleMenuList.add(roleMenu);
        }
        sysRoleMenuMapper.insert(roleMenuList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(Long id){
        // 权限校验
        List<SysRoleBaseListResp> userRolePermissionList = baseMapper.selectRoleList(null);
        if (!userRolePermissionList.stream().map(SysRoleBaseListResp::getId).toList().contains(id)){
            throw new ISystemException("没有该角色权限");
        }
        baseMapper.deleteById(id);
        // 删除菜单关系
        ChainWrappers.lambdaUpdateChain(sysRoleMenuMapper).eq(SysRoleMenu::getRoleId, id).remove();
        // 删除人员关系
        ChainWrappers.lambdaUpdateChain(sysUserRoleMapper).eq(SysUserRole::getRoleId, id).remove();
    }

    @Override
    public void updateStatus(Long id, SysStatusEnum status){
        // 权限校验
        List<SysRoleBaseListResp> userRolePermissionList = baseMapper.selectRoleList(null);
        if (!userRolePermissionList.stream().map(SysRoleBaseListResp::getId).toList().contains(id)){
            throw new ISystemException("没有该角色权限");
        }
        // 更新状态
        lambdaUpdate().set(SysRole::getStatus, status).eq(SysRole::getId, id).update();
    }

    /**
     * 校验添加编辑角色权限
     * @param req
     */
    private void check(SysRoleAddReq req){
        // 要求新增的角色权限范围和菜单权限不能大于添加者本身
        DataScopeEnum reqDataScope = req.getDataScope();
        // 获取当前用户所有的数据权限
        List<SysUserRolePermission> userRolePermissionList = stpInterface.getRolePermissionList(UserUtil.getUserId());
        List<DataScopeEnum> userDataScopeList = userRolePermissionList.stream().map(SysUserRolePermission::getDataScope).distinct().toList();
        // 先判断是否为自定义权限，自定义权限需要判断部门的包含关系
        if (reqDataScope == DataScopeEnum.CUSTOM){
            // 自定义权限，本人的部门必须全包含添加的角色的部门
            Set<Long> deptIdList = req.getDeptIdList();
            if (deptIdList == null || deptIdList.isEmpty()){
                throw new ISystemException("自定义权限部门不能为空");
            }
            if (!new HashSet<>(sysDeptMapper.selectIdList()).containsAll(deptIdList)){
                throw new ISystemException("自定义权限，部门权限超出");
            }
        }else {
            // 非自定义权限，那就只剩下 1、3、4、5三种权限可能，使用大小进行判断
            DataScopeEnum userDataScopeMax = userDataScopeList.stream().filter(o -> o != DataScopeEnum.CUSTOM).min((o1, o2) -> CompareUtil.compare(o1.getCode(), o2.getCode())).get();
            if (reqDataScope.getCode() < userDataScopeMax.getCode()){
                // 超出本人权限范围
                throw new ISystemException("超出本人权限范围");
            }
        }

        // 判断菜单权限
        if (req.getMenuIdList() != null && !req.getMenuIdList().isEmpty()){
            // 获取我拥有的所有菜单
            List<Long> userRoleIdList = stpInterface.getRolePermissionList(UserUtil.getUserId()).stream().map(SysUserRolePermission::getId).toList();
            List<SysMenuListResp> menuList = sysRoleMenuMapper.selectMenuListByRoleIds(userRoleIdList, null);
            if (!new HashSet<>(menuList.stream().map(SysMenuListResp::getId).toList()).containsAll(req.getMenuIdList())){
                throw new ISystemException("超出本人菜单权限");
            }
        }
    }

}




