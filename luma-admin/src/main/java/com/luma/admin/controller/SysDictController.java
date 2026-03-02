package com.luma.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.luma.common.utils.MapstructUtil;
import com.luma.system.domain.entity.SysDict;
import com.luma.system.domain.vo.SysAddReq;
import com.luma.system.domain.vo.SysDictBaseTreeResp;
import com.luma.system.domain.vo.SysDictTreeResp;
import com.luma.system.domain.vo.SysEditReq;
import com.luma.system.service.SysDictService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统字典
 * @author i-become
 */
@Validated
@RestController
@RequestMapping("/dict")
public class SysDictController {

    @Resource
    private SysDictService sysDictService;

    /**
     * 添加字典
     * @param req 字典信息
     * @return 字典编号
     */
    @PostMapping
    @SaCheckPermission("system:dict:add")
    public Integer add(@RequestBody SysAddReq req){
        return sysDictService.add(req);
    }

    /**
     * 编辑字典
     * @param id 字典编号
     * @param req 字典信息
     */
    @PutMapping("/{id}")
    @SaCheckPermission("system:dict:edit")
    public void edit(@PathVariable Integer id, @RequestBody SysEditReq req){
        SysDict sysDict = MapstructUtil.convert(req, SysDict.class);
        sysDict.setId(id);
        sysDictService.updateById(sysDict);
    }

    /**
     * 删除字典及所有子孙节点
     * @param id 字典编号
     */
    @DeleteMapping("/{id}")
    @SaCheckPermission("system:dict:remove")
    public void remove(@PathVariable Integer id){
        sysDictService.remove(id);
    }

    /**
     * 获取字典树
     * @param key 指定key
     * @param level 指定层级
     * @param format 返回格式：full(完整信息，默认) 或 simple(简化信息，用于下拉框)
     * @return 字典列表
     */
    @GetMapping
    public Object tree(String key, Integer level, @RequestParam(defaultValue = "full") String format){
        if ("simple".equals(format)) {
            return sysDictService.baseTree(key, level);
        }
        return sysDictService.tree(key, level);
    }

}
