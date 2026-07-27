package com.luma.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.luma.common.annotation.DataScope;
import com.luma.common.constant.SatokenConstant;
import com.luma.common.exception.system.ISystemException;
import com.luma.common.utils.MapstructUtil;
import com.luma.framework.permission.IStpInterface;
import com.luma.framework.permission.TenantContextHolder;
import com.luma.framework.utils.UserUtil;
import com.luma.system.domain.bo.SysUserLoginClient;
import com.luma.system.domain.entity.*;
import com.luma.system.domain.vo.*;
import com.luma.system.enums.SysStatusEnum;
import com.luma.system.mapper.*;
import com.luma.system.service.SysUserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
* @author i-become
* @description 针对表【sys_user(用户信息表)】的数据库操作Service实现
* @createDate 2024-07-31 12:00:51
*/
@Slf4j
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService{

    @Resource
    private SysDeptMapper sysDeptMapper;

    @Resource
    private SysUserRoleMapper sysUserRoleMapper;

    @Resource
    private SysPostMapper sysPostMapper;

    @Resource
    private SysUserPostMapper sysUserPostMapper;

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Resource
    private SysTenantMapper sysTenantMapper;

    @Resource
    private IStpInterface stpInterface;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysUserLoginResp login(SysUserLoginReq req, String loginIp){
        // 密码校验
        TenantContextHolder.setTenantId(req.getTenantId());
        SysUser user = lambdaQuery().select(SysUser::getId, SysUser::getNickname, SysUser::getPassword, SysUser::getStatus, SysUser::getDeptId, SysUser::getTenantId)
                .eq(SysUser::getLoginName, req.getLoginName())
                .one();
        if (user == null){
            // 用户不存在
            throw new ISystemException("用户名或密码错误");
        }
        if (!SmUtil.sm3(req.getPassword()).equals(user.getPassword())){
            // 密码校验失败
            throw new ISystemException("用户名或密码错误");
        }
        // 密码校验通过，校验用户状态
        if (SysStatusEnum.DISABLED == user.getStatus()){
            throw new ISystemException("用户处于被禁用状态");
        }
        // 登录，保存登录信息
        StpUtil.login(user.getId(), SaLoginParameter.create()
                .setExtra("deptId", user.getDeptId())
                .setExtra("tenantId", user.getTenantId())
                .setExtra("nickname", user.getNickname()));
        SysUser loginUser = new SysUser();
        loginUser.setId(user.getId());
        loginUser.setLoginIp(loginIp);
        loginUser.setLoginDate(LocalDateTime.now());
        baseMapper.updateById(loginUser);
        // 获取用户权限信息
        return SysUserLoginResp.builder()
                .token(StpUtil.getTokenInfo().getTokenValue())
                .expiresIn(StpUtil.getTokenActiveTimeout())
                .userInfo(SysLoginUserInfoResp.builder()
                        .userId(user.getId())
                        .nickname(user.getNickname())
                        .roles(StpUtil.getRoleList())
                        .build())
                .build();
    }

    @Override
    public void logout(String userId) {
        // 获取当前用户登录的应用列表
        // "satoken:oauth2:user:login-client:1"
//        String userId = StpUtil.getLoginIdAsString();
        String loginClientKey = SatokenConstant.getOauth2LoginClientKey(userId);
        List<SysUserLoginClient> loginClientList = (List<SysUserLoginClient>)StpUtil.stpLogic.getSaTokenDao().getObject(loginClientKey);
        if (loginClientList != null){
            // 逐个通知注销
            for (SysUserLoginClient loginClient : loginClientList) {
                notifyClientLogout(userId, loginClient);
            }
        }
        // 注销平台
        StpUtil.logout(userId);
        // 删除关系
        StpUtil.stpLogic.getSaTokenDao().deleteObject(loginClientKey);
    }

    /**
     * 通知应用注销用户
     * @param userId
     * @param loginClient
     */
    private void notifyClientLogout(String userId, SysUserLoginClient loginClient){
        String url = loginClient.getLogoutCall();
        if (StrUtil.isBlank(url)){
            return;
        }
        // 参数 & 签名
        Map<String, Object> paramsMap = new TreeMap<>();
        paramsMap.put("clientId", loginClient.getClientId());
        paramsMap.put("userId", userId);
        paramsMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
        paramsMap.put("nonceStr", SaFoxUtil.getRandomString(32));
        paramsMap.put("sign", SecureUtil.signParamsMd5(paramsMap, "&key=" + loginClient.getSecret()));
        // 发起请求
        try {
            HttpUtil.createGet(url).form(paramsMap).execute();
        }catch (Exception e){
            log.error("is-system-单点注销通知应用响应错误，clientId:{},error:{}", loginClient.getClientId(), e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    @DataScope(deptAlias = "d", deptIdColumnName = "id", userAlias = "u", userIdColumnName = "id")
    public IPage<SysUserPageResp> page(SysUserPageReq req){
        return baseMapper.selectUserPage(req.toMpPage(), req);
    }

    @Override
    @Transactional(readOnly = true)
    public SysUserInfoResp userInfo(Long id){
        // 权限判断
        checkEditUser(id);
        SysUserInfoResp resp = baseMapper.selectUserinfo(id);
        if (resp == null){
            throw new ISystemException("用户信息不存在");
        }
        // 角色信息
        resp.setRoles(StpUtil.getRoleList(id));
        // 岗位信息
        resp.setPosts(sysPostMapper.selectPostListByUserId(id));
        // 权限信息
        resp.setPerms(stpInterface.getPermissionList(id, null));
        // 租户别名
        resp.setTenantAlias(ChainWrappers.lambdaQueryChain(sysTenantMapper).select(SysTenant::getId, SysTenant::getAlias).eq(SysTenant::getId, resp.getTenantId()).one().getAlias());
        return resp;
    }

    @Override
    public Boolean isDisable(Long userId){
        return lambdaQuery().select(SysUser::getStatus).one().getStatus() == SysStatusEnum.DISABLED;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(SysUserAddReq req){
        // 账号重复性校验
        if (lambdaQuery().eq(SysUser::getLoginName, req.getLoginName()).exists()){
            throw new ISystemException("该账号已存在");
        }
        checkData(req.getDeptId(), req.getRoleIds(), req.getPostIds());
        // 保存用户
        SysUser sysUser = MapstructUtil.convert(req, SysUser.class);
        sysUser.setId(IdUtil.getSnowflakeNextId());
        sysUser.setPassword(SmUtil.sm3(req.getPassword()));
        baseMapper.insert(sysUser);
        // 保存用户和角色关系
        if (req.getRoleIds() == null){
            return sysUser.getId();
        }
        List<SysUserRole> userRoleList = new ArrayList<>();
        for (Long roleId : req.getRoleIds()){
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setUserId(sysUser.getId());
            sysUserRole.setRoleId(roleId);
            userRoleList.add(sysUserRole);
        }
        sysUserRoleMapper.insert(userRoleList);
        // 保存用户和岗位关系
        if (req.getPostIds() == null){
            return sysUser.getId();
        }
        List<SysUserPost> sysUserPostList = new ArrayList<>();
        for (Long postId : req.getPostIds()){
            SysUserPost sysUserPost = new SysUserPost();
            sysUserPost.setUserId(sysUser.getId());
            sysUserPost.setPostId(postId);
            sysUserPostList.add(sysUserPost);
        }
        sysUserPostMapper.insert(sysUserPostList);
        return sysUser.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "luma:user:roles", key = "#id", condition = "#req.roleIds != null")
    public void edit(Long id, SysUserEditReq req){
        // 获取原有数据，判断权限是否满足
        SysUser sysUser = lambdaQuery().select(SysUser::getId, SysUser::getDeptId).eq(SysUser::getId, id).one();
        if (sysUser == null){
            throw new ISystemException("用户不存在");
        }
        List<Long> iDeptIdList = sysDeptMapper.selectIdList();
        if (!iDeptIdList.contains(sysUser.getDeptId())){
            throw new ISystemException("权限不够");
        }
        checkData(req.getDeptId(), req.getRoleIds(), req.getPostIds());
        // 如果是自己，不能修改启用状态
        if (Objects.equals(id, UserUtil.getUserId())){
            sysUser.setStatus(null);
        }
        // 修改用户信息
        sysUser = MapstructUtil.convert(req, SysUser.class);
        sysUser.setId(id);
        baseMapper.updateById(sysUser);
        // 下面修改角色和岗位，用户不能修改自身
        if (Objects.equals(id, UserUtil.getUserId())){
            return;
        }
        // 保存用户和角色关系
        if (req.getRoleIds() != null){
            List<SysUserRole> userRoleList = new ArrayList<>();
            for (Long roleId : req.getRoleIds()){
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setUserId(sysUser.getId());
                sysUserRole.setRoleId(roleId);
                userRoleList.add(sysUserRole);
            }
            ChainWrappers.lambdaUpdateChain(sysUserRoleMapper).eq(SysUserRole::getUserId, id).remove();
            sysUserRoleMapper.insert(userRoleList);
        }
        // 保存用户和岗位关系
        if (req.getPostIds() != null){
            ChainWrappers.lambdaUpdateChain(sysUserPostMapper).eq(SysUserPost::getUserId, id).remove();
            List<SysUserPost> sysUserPostList = new ArrayList<>();
            for (Long postId : req.getPostIds()){
                SysUserPost sysUserPost = new SysUserPost();
                sysUserPost.setUserId(sysUser.getId());
                sysUserPost.setPostId(postId);
                sysUserPostList.add(sysUserPost);
            }
            sysUserPostMapper.insert(sysUserPostList);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "luma:user:roles", key = "#id")
    public void remove(Long id){
        // 获取原有数据，判断权限是否满足
        SysUser sysUser = lambdaQuery().select(SysUser::getId, SysUser::getDeptId).eq(SysUser::getId, id).one();
        if (sysUser == null){
            throw new ISystemException("用户不存在");
        }
        List<Long> iDeptIdList = sysDeptMapper.selectIdList();
        if (!iDeptIdList.contains(sysUser.getDeptId())){
            throw new ISystemException("权限不够");
        }
        // 删除用户
        baseMapper.deleteById(id);
        // 删除用户关联的角色
        ChainWrappers.lambdaUpdateChain(sysUserRoleMapper).eq(SysUserRole::getUserId, id).remove();
        // 删除用户关联的岗位
        ChainWrappers.lambdaUpdateChain(sysUserPostMapper).eq(SysUserPost::getUserId, id).remove();
    }

    @Override
    public void resetPassword(Long id, String newPassword){
        // 修改密码
        lambdaUpdate().set(SysUser::getPassword, SmUtil.sm3(newPassword)).set(SysUser::getPwdUpdateDate, LocalDateTime.now()).eq(SysUser::getId, id).update();
    }

    @Override
    @CacheEvict(cacheNames = "luma:user:roles", key = "#id")
    public void updateStatus(Long id, SysStatusEnum status){
        // 如果是自己，不能修改启用状态
        if (Objects.equals(id, UserUtil.getUserId())){
            throw new ISystemException("无法修改自身状态");
        }
        checkEditUser(id);
        // 修改用户状态
        lambdaUpdate().set(SysUser::getStatus, status).eq(SysUser::getId, id).update();
    }

    /**
     * 数据有效性校验
     * @param deptId 部门编号
     * @param roleIds 角色编号列表
     * @param postIds 岗位编号列表
     */
    private void checkData(Long deptId, Set<Long> roleIds, Set<Long> postIds){
        // 部门有效性校验
        if (!sysDeptMapper.selectIdList().contains(deptId)){
            throw new ISystemException("部门填写错误，超出本人权限");
        }
        // 角色有效性校验
        if (roleIds != null && !roleIds.isEmpty()){
            if (!new HashSet<>(sysRoleMapper.selectRoleList(null).stream().map(SysRoleBaseListResp::getId).toList()).containsAll(roleIds)){
                throw new ISystemException("角色填写错误，超出本人权限");
            }
        }
        // 岗位有效性验证
        if (postIds != null && !postIds.isEmpty()){
            List<SysPost> postList = ChainWrappers.lambdaQueryChain(sysPostMapper).select(SysPost::getId, SysPost::getPostName, SysPost::getStatus).in(SysPost::getId, postIds).list();
            if (postList.size() != postIds.size()){
                throw new ISystemException("岗位信息错误");
            }
            for (SysPost post : postList){
                if (post.getStatus() == SysStatusEnum.DISABLED){
                    throw new ISystemException(String.format("岗位“%s”被停用", post.getPostName()));
                }
            }
        }
    }

    /**
     * 校验是否有操作目标用户的权限
     * @param userId 用户编号
     */
    private void checkEditUser(Long userId){
        // 权限判断
        if (!Objects.equals(userId, UserUtil.getUserId())){
            SysUser sysUser = lambdaQuery().select(SysUser::getId, SysUser::getDeptId).eq(SysUser::getId, userId).one();
            if (!sysDeptMapper.selectIdList().contains(sysUser.getDeptId())){
                throw new ISystemException("您无此用户权限");
            }
        }
    }

}




