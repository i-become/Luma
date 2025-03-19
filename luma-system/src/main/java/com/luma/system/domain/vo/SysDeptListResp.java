package com.luma.system.domain.vo;

import com.luma.system.domain.entity.SysDept;
import com.luma.system.enums.SysStatusEnum;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

/**
 * @author i-become
 */
@AutoMapper(target = SysDept.class)
@Data
public class SysDeptListResp {

    /**
     * 部门id
     */
    private Long id;

    /**
     * 父部门id
     */
    private Long parentId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 显示顺序
     */
    private Integer sort;

    /**
     * 负责人
     */
    private String leader;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 部门状态（0正常 1停用）
     */
    private SysStatusEnum status;

}
