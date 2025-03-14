package com.luma.system.mapper;

import com.luma.common.annotation.DataScope;
import com.luma.system.domain.entity.SysDept;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.luma.system.domain.vo.SysDeptBaseListResp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 刘靖
* @description 针对表【sys_dept(部门表)】的数据库操作Mapper
* @createDate 2024-08-01 15:51:52
* @Entity com.luma.system.domain.entity.SysDept
*/
public interface SysDeptMapper extends BaseMapper<SysDept> {

    /**
     * 获取部门编号列表
     * @return
     */
    @DataScope(deptIdColumnName = "id")
    List<Long> selectIdList();

    /**
     * 获取部门列表，如果传入角色编号，会返回部门有没有关联该角色
     * @param roleId 角色编号
     * @return
     */
    @DataScope(deptAlias = "d", deptIdColumnName = "id", autoSql = false)
    List<SysDeptBaseListResp> selectBaseList(Long roleId);

    /**
     * 更新祖籍编号
     * 同时会更新所有子孙节点的祖籍编号
     * @param id 部门编号
     * @param parentAncestors 父级祖籍编号
     * @param oldParentAncestorsLength 原有父级祖籍编号长度
     * @return
     */
    int updateDeptAncestors(@Param("id") Long id, @Param("parentAncestors") String parentAncestors, @Param("oldParentAncestorsLength") Integer oldParentAncestorsLength);

}




