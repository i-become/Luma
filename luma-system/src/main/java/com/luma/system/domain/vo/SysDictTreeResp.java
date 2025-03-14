package com.luma.system.domain.vo;

import com.luma.system.domain.bo.SysDictTreeBo;
import com.luma.system.enums.SysDictTypeEnum;
import com.luma.system.enums.SysStatusEnum;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author 刘靖
 */
@AutoMapper(target = SysDictTreeBo.class)
@Data
public class SysDictTreeResp {

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
     * 状态（0：正常，1：禁用）
     */
    private SysStatusEnum status;

    /**
     * 是否为系统内置
     */
    private Boolean sys;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
