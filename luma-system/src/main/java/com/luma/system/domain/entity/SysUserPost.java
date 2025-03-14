package com.luma.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 用户与岗位关联表
 * @TableName sys_user_post
 */
@TableName(value ="sys_user_post")
@Data
public class SysUserPost implements Serializable {
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 岗位ID
     */
    private Long postId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
