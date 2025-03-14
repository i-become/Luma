package com.luma.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import com.luma.common.domain.TenantBaseEntity;
import com.luma.system.enums.SysStatusEnum;
import lombok.Data;

/**
 * 岗位信息表
 * @TableName sys_post
 */
@TableName(value ="sys_post")
@Data
public class SysPost extends TenantBaseEntity implements Serializable {
    /**
     * 岗位ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 岗位编码
     */
    private String code;

    /**
     * 岗位名称
     */
    private String postName;

    /**
     * 显示顺序
     */
    private Integer sort;

    /**
     * 状态（0正常 1停用）
     */
    private SysStatusEnum status;

}
