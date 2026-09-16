package com.luma.system.domain.vo;

import com.luma.system.domain.entity.SysPost;
import com.luma.system.enums.SysStatusEnum;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author i-become
 */
@AutoMapper(target = SysPost.class)
@Data
public class SysPostAddReq {

    /**
     * 岗位编码
     */
    @NotBlank(message = "{validation.post.code.NotBlank}")
    private String code;

    /**
     * 岗位名称
     */
    @NotBlank(message = "{validation.post.postName.NotBlank}")
    private String postName;

    /**
     * 显示顺序
     */
    @NotNull(message = "{validation.sort.NotNull}")
    private Integer sort;

    /**
     * 岗位状态（0正常 1停用）
     */
    @NotNull(message = "{validation.post.status.NotNull}")
    private SysStatusEnum status;

}
