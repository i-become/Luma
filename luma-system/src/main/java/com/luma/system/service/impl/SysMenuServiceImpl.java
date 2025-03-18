package com.luma.system.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luma.common.domain.SysUserAuthRoleInfo;
import com.luma.common.exception.system.ISystemException;
import com.luma.common.utils.MapstructUtil;
import com.luma.framework.permission.IStpInterface;
import com.luma.framework.utils.UserUtil;
import com.luma.system.domain.entity.SysMenu;
import com.luma.system.domain.vo.SysMenuAddReq;
import com.luma.system.domain.vo.SysMenuBaseListResp;
import com.luma.system.domain.vo.SysMenuListResp;
import com.luma.system.mapper.SysMenuMapper;
import com.luma.system.mapper.SysRoleMenuMapper;
import com.luma.system.service.SysMenuService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 刘靖
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

    @Override
    @Transactional(readOnly = true)
    public List<SysMenuListResp> list(String name){
        // 是否为管理员
        if (UserUtil.isAdmin()){
            return lambdaQuery().select(SysMenu::getId, SysMenu::getParentId, SysMenu::getName, SysMenu::getRedirect, SysMenu::getComponent, SysMenu::getIcon, SysMenu::getSort, SysMenu::getTitle,
                    SysMenu::getTarget, SysMenu::getActive, SysMenu::getType, SysMenu::getPath, SysMenu::getIsHide, SysMenu::getIsFull, SysMenu::getIsAffix, SysMenu::getIsKeepAlive, SysMenu::getTag, SysMenu::getPerms)
                    .like(StringUtils.isNotBlank(name), SysMenu::getName, name)
                    .list().stream().map(o -> o.to(SysMenuListResp.class)).toList();
        }
        // 获取我拥有的所有菜单
        List<Long> roleIds = stpInterface.getUserAuthInfo(UserUtil.getUserId()).getRoles().stream().map(SysUserAuthRoleInfo::getId).toList();
        return sysRoleMenuMapper.selectMenuListByRoleIds(roleIds, name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SysMenuBaseListResp> baseList(Long roleId){
        // 判断是否有该角色权限
        if (!UserUtil.isAdmin() && !stpInterface.getUserAuthInfo(UserUtil.getUserId()).getRoles().stream().map(SysUserAuthRoleInfo::getId).toList().contains(roleId)) {
            throw new ISystemException("没有该角色权限");
        }
        return baseMapper.selectBaseList(roleId);
    }

    @Override
    public void add(SysMenuAddReq req){
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
        sysMenu.setId(IdUtil.getSnowflake().nextId());
        this.save(sysMenu);
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
