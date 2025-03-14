package com.luma.system.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.luma.common.annotation.DataScope;
import com.luma.system.domain.entity.SysRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.luma.system.domain.vo.SysRoleBaseListResp;
import com.luma.system.domain.vo.SysRolePageReq;
import com.luma.system.domain.vo.SysRolePageResp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 刘靖
* @description 针对表【sys_role(角色信息表)】的数据库操作Mapper
* @createDate 2024-08-01 15:51:52
* @Entity com.luma.system.domain.entity.SysRole
*/
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 查询角色分页
     * @param page
     * @param req
     * @return
     */
    IPage<SysRolePageResp> selectRolePage(IPage page, @Param("req") SysRolePageReq req);

    /**
     * 查询角色列表
     * @return
     */
    @DataScope(deptAlias = "su", autoSql = false)
    List<SysRoleBaseListResp> selectRoleList();

    /**
     * 查询角色列表
     * @param userId 用户编号 不传为查所有
     * @return
     */
    List<SysRoleBaseListResp> selectRoleListByUserId(Long userId);

}




