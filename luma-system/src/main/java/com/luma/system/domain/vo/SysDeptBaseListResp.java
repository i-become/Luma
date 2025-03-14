package com.luma.system.domain.vo;

import lombok.Data;

/**
 * @author 刘靖
 */
@Data
public class SysDeptBaseListResp {

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
     * 是否选关联该角色
     */
    private Boolean checked;

}
