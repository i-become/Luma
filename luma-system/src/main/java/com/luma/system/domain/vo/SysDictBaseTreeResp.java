package com.luma.system.domain.vo;

import com.luma.system.domain.bo.SysDictTreeBo;
import com.luma.system.enums.SysDictTypeEnum;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

/**
 * @author i-become
 */
@AutoMapper(target = SysDictTreeBo.class)
@Data
public class SysDictBaseTreeResp {

    /**
     * 字典编号
     */
    private Integer id;

    /**
     * 父级id，约定顶层为0
     */
    private Integer parentId;

    /**
     * 名称
     */
    private String name;

    /**
     * 字典类型
     */
    private SysDictTypeEnum type;

    /**
     * 上级节点key
     */
    private String parentKey;

    /**
     * 字典键
     */
    private String key;

    /**
     * 字典值
     */
    private Object value;

    /**
     * 排序
     */
    private Integer sort;

}
