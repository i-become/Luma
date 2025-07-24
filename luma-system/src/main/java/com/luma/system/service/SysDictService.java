package com.luma.system.service;

import com.luma.system.domain.entity.SysDict;
import com.baomidou.mybatisplus.extension.service.IService;
import com.luma.system.domain.vo.SysAddReq;
import com.luma.system.domain.vo.SysDictBaseTreeResp;
import com.luma.system.domain.vo.SysDictTreeResp;

import java.util.List;

/**
* @author 92755
* @description 针对表【sys_dict】的数据库操作Service
* @createDate 2025-01-22 11:24:46
*/
public interface SysDictService extends IService<SysDict> {

    /**
     * 新增字典
     * @param req 字典信息
     * @return 字典编号
     */
    Integer add(SysAddReq req);

    /**
     * 删除字典及所有子孙节点
     * @param id 字典编号
     */
    void remove(Integer id);

    /**
     * 获取字典树
     * @param key 指定key
     * @param level 指定层级
     * @return 字典列表
     */
    List<SysDictTreeResp> tree(String key, Integer level);

    /**
     * 获取字典树（最小单元）
     * @param key 指定key
     * @param level 指定层级
     * @return 字典列表
     */
    List<SysDictBaseTreeResp> baseTree(String key, Integer level);
}
