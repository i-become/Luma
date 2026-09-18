package com.luma.system.domain.vo;

import com.luma.common.domain.BasePage;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 应用分页查询
 * @author i-become
 */
@Data
public class ClientPageReq extends BasePage {

    /**
     * 应用名称
     */
    private String name;

    /**
     * 创建时间 - 开始
     */
    private LocalDateTime startCreateTime;

    /**
     * 创建时间 - 结束
     */
    private LocalDateTime endCreateTime;

}
