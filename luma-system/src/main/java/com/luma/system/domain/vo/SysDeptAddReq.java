package com.luma.system.domain.vo;

import com.luma.system.domain.entity.SysDept;
import com.luma.system.enums.SysStatusEnum;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author 刘靖
 */
@AutoMapper(target = SysDept.class)
@Data
public class SysDeptAddReq {

    /**
     * 父部门id
     */
    @NotNull(message = "上级部门不能为空")
    private Long parentId;

    /**
     * 部门名称
     */
    @NotNull(message = "部门名称不能为空")
    private String deptName;

    /**
     * 显示顺序
     */
    @NotNull(message = "显示顺序不能为空")
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
    @NotNull(message = "部门状态不能为空")
    private SysStatusEnum status;

}
