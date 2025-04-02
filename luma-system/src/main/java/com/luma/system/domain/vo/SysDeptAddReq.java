package com.luma.system.domain.vo;

import com.luma.system.domain.entity.SysDept;
import com.luma.system.enums.SysStatusEnum;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author i-become
 */
@AutoMapper(target = SysDept.class)
@Data
public class SysDeptAddReq {

    /**
     * 父部门id
     */
    @NotNull(message = "{validation.dept.parentId.NotNull}")
    private Long parentId;

    /**
     * 部门名称
     */
    @NotBlank(message = "{validation.dept.deptName.NotBlank}")
    private String deptName;

    /**
     * 显示顺序
     */
    @NotNull(message = "{validation.sort.NotNull}")
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
    @NotNull(message = "{validation.dept.status.NotNull}")
    private SysStatusEnum status;

}
