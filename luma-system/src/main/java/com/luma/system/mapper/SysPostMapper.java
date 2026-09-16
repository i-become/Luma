package com.luma.system.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.luma.system.domain.entity.SysPost;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.luma.system.domain.vo.SysPostBaseListResp;
import com.luma.system.domain.vo.SysPostListResp;
import com.luma.system.domain.vo.SysPostPageReq;
import com.luma.system.domain.vo.SysPostPageResp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 针对表【sys_post(岗位信息表)】的数据库操作Mapper
 * @author i-become
 */
public interface SysPostMapper extends BaseMapper<SysPost> {

    /**
     * 查询岗位分页
     * @param page 分页参数
     * @param req 查询条件
     * @return 岗位分页
     */
    IPage<SysPostPageResp> selectPostPage(IPage page, @Param("req") SysPostPageReq req);

    /**
     * 查询用户的岗位列表
     * @param userId 用户编号
     * @return 岗位列表
     */
    List<SysPostListResp> selectPostListByUserId(Long userId);

    /**
     * 查询岗位基础列表
     * @return 岗位列表
     */
    List<SysPostBaseListResp> selectPostBaseList();

}
