package com.luma.system.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.luma.system.domain.entity.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.luma.system.domain.vo.SysUserInfoResp;
import com.luma.system.domain.vo.SysUserPageReq;
import com.luma.system.domain.vo.SysUserPageResp;
import org.apache.ibatis.annotations.Param;

/**
* @author i-become
* @description 针对表【sys_user(用户信息表)】的数据库操作Mapper
* @createDate 2024-07-31 12:00:51
* @Entity com.luma.system.domain.entity.SysUser
*/
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 用户分页
     * @param page
     * @param req
     * @return
     */
    IPage<SysUserPageResp> selectUserPage(IPage page, @Param("req") SysUserPageReq req);

    /**
     * 用户详情
     * @param id 用户编号
     * @return
     */
    SysUserInfoResp selectUserinfo(Long id);

}




