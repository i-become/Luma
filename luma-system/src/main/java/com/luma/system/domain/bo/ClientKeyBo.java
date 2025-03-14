package com.luma.system.domain.bo;

import com.luma.system.domain.entity.Client;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

/**
 * @author 刘靖
 */
@AutoMapper(target = Client.class)
@Data
public class ClientKeyBo {

    /**
     * 应用编号
     */
    private Long id;

    /**
     * 应用密钥
     */
    private String secret;

    /**
     * 数据推送地址
     */
    private String pushUrl;

}
