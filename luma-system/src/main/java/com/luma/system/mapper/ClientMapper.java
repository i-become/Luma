package com.luma.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.luma.system.domain.entity.Client;
import com.luma.system.domain.vo.ClientBaseListResp;
import com.luma.system.domain.vo.ClientPageReq;
import com.luma.system.domain.vo.ClientPageResp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 针对表【client(应用表)】的数据库操作Mapper
 * @author i-become
 */
public interface ClientMapper extends BaseMapper<Client> {

    /**
     * 查询应用分页
     * @param page 分页参数
     * @param req 查询条件
     * @return 应用分页
     */
    IPage<ClientPageResp> selectClientPage(IPage page, @Param("req") ClientPageReq req);

    /**
     * 查询应用基础列表
     * @return 应用列表
     */
    List<ClientBaseListResp> selectClientBaseList();

}
