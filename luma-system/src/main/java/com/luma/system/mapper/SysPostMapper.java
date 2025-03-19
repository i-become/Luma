package com.luma.system.mapper;

import com.luma.system.domain.entity.SysPost;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.luma.system.domain.vo.SysPostListResp;

import java.util.List;

/**
* @author i-become
* @description 针对表【sys_post(岗位信息表)】的数据库操作Mapper
* @createDate 2024-08-01 15:51:52
* @Entity com.luma.system.domain.entity.SysPost
*/
public interface SysPostMapper extends BaseMapper<SysPost> {

    /**
     * 查询用户的部门列表
     * @param userId
     * @return
     */
    List<SysPostListResp> selectPostListByUserId(Long userId);

}




