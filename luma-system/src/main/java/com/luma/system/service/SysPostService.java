package com.luma.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.luma.system.domain.entity.SysPost;
import com.baomidou.mybatisplus.extension.service.IService;
import com.luma.system.domain.vo.SysPostAddReq;
import com.luma.system.domain.vo.SysPostBaseListResp;
import com.luma.system.domain.vo.SysPostPageReq;
import com.luma.system.domain.vo.SysPostPageResp;
import com.luma.system.enums.SysStatusEnum;

import java.util.List;

/**
 * 针对表【sys_post(岗位信息表)】的数据库操作Service
 * @author i-become
 */
public interface SysPostService extends IService<SysPost> {

    /**
     * 获取岗位分页
     * @param req 查询信息
     * @return 岗位分页
     */
    IPage<SysPostPageResp> page(SysPostPageReq req);

    /**
     * 获取岗位基础列表
     * @return 岗位列表
     */
    List<SysPostBaseListResp> getBaseList();

    /**
     * 添加岗位
     * @param req 岗位信息
     * @return 岗位编号
     */
    Long add(SysPostAddReq req);

    /**
     * 编辑岗位
     * @param id 岗位编号
     * @param req 岗位信息
     */
    void edit(Long id, SysPostAddReq req);

    /**
     * 删除岗位
     * @param id 岗位编号
     */
    void remove(Long id);

    /**
     * 更新岗位状态
     * @param id 岗位编号
     * @param status 岗位状态
     */
    void updateStatus(Long id, SysStatusEnum status);

}
