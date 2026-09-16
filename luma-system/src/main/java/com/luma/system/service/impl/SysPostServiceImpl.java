package com.luma.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.luma.common.exception.system.ISystemException;
import com.luma.common.utils.MapstructUtil;
import com.luma.system.domain.entity.SysPost;
import com.luma.system.domain.entity.SysUserPost;
import com.luma.system.domain.vo.SysPostAddReq;
import com.luma.system.domain.vo.SysPostBaseListResp;
import com.luma.system.domain.vo.SysPostPageReq;
import com.luma.system.domain.vo.SysPostPageResp;
import com.luma.system.enums.SysStatusEnum;
import com.luma.system.mapper.SysPostMapper;
import com.luma.system.mapper.SysUserPostMapper;
import com.luma.system.service.SysPostService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 针对表【sys_post(岗位信息表)】的数据库操作Service实现
 * @author i-become
 */
@Service
public class SysPostServiceImpl extends ServiceImpl<SysPostMapper, SysPost>
    implements SysPostService{

    @Resource
    private SysUserPostMapper sysUserPostMapper;

    @Override
    @Transactional(readOnly = true)
    public IPage<SysPostPageResp> page(SysPostPageReq req){
        return baseMapper.selectPostPage(req.toMpPage(), req);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SysPostBaseListResp> getBaseList(){
        return baseMapper.selectPostBaseList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(SysPostAddReq req){
        // 判断岗位编码是否重复
        if (lambdaQuery().eq(SysPost::getCode, req.getCode()).exists()){
            throw new ISystemException("exception.post.code.exists");
        }
        SysPost sysPost = MapstructUtil.convert(req, SysPost.class);
        baseMapper.insert(sysPost);
        return sysPost.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(Long id, SysPostAddReq req){
        if (!lambdaQuery().eq(SysPost::getId, id).exists()){
            throw new ISystemException("exception.post.notFound");
        }
        // 判断岗位编码是否重复
        if (lambdaQuery().eq(SysPost::getCode, req.getCode()).ne(SysPost::getId, id).exists()){
            throw new ISystemException("exception.post.code.exists");
        }
        SysPost sysPost = MapstructUtil.convert(req, SysPost.class);
        sysPost.setId(id);
        baseMapper.updateById(sysPost);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(Long id){
        if (!lambdaQuery().eq(SysPost::getId, id).exists()){
            throw new ISystemException("exception.post.notFound");
        }
        // 判断岗位是否已分配给用户
        if (ChainWrappers.lambdaQueryChain(sysUserPostMapper).eq(SysUserPost::getPostId, id).exists()){
            throw new ISystemException("exception.post.assigned.cannotDelete");
        }
        baseMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, SysStatusEnum status){
        if (!lambdaQuery().eq(SysPost::getId, id).exists()){
            throw new ISystemException("exception.post.notFound");
        }
        lambdaUpdate().set(SysPost::getStatus, status).eq(SysPost::getId, id).update();
    }

}
