package com.luma.system.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luma.common.exception.system.ISystemException;
import com.luma.common.utils.MapstructUtil;
import com.luma.system.domain.bo.SysDictTreeBo;
import com.luma.system.domain.entity.SysDict;
import com.luma.system.domain.vo.SysAddReq;
import com.luma.system.domain.vo.SysDictBaseTreeResp;
import com.luma.system.domain.vo.SysDictTreeResp;
import com.luma.system.enums.SysDictTypeEnum;
import com.luma.system.service.SysDictService;
import com.luma.system.mapper.SysDictMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author 92755
* @description 针对表【sys_dict】的数据库操作Service实现
* @createDate 2025-01-22 11:24:46
*/
@Service
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDict>
    implements SysDictService{

    public static final int BASE_ID = 0;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(SysAddReq req){
        // 如果上级节点不是顶级节点，判断节点是否存在
        SysDict sysDict = MapstructUtil.convert(req, SysDict.class);
        String parentAncestors = null;
        if (sysDict.getParentId() == null || sysDict.getParentId() == BASE_ID){
            sysDict.setParentId(BASE_ID);
            sysDict.setLevel(BASE_ID);
            parentAncestors = BASE_ID + ",";
        }else {
            SysDict parentDict = lambdaQuery().select(SysDict::getId, SysDict::getLevel, SysDict::getAncestors, SysDict::getKey).eq(SysDict::getId, sysDict.getParentId()).one();
            if (parentDict == null){
                throw new ISystemException("上级字典不存在");
            }
            sysDict.setLevel(parentDict.getLevel() + 1);
            sysDict.setParentKey(parentDict.getKey());
            parentAncestors = parentDict.getAncestors();
        }
        // 判断键是否重复
        if (lambdaQuery().eq(SysDict::getKey, sysDict.getKey()).exists()){
            throw new ISystemException("该字典键已经存在");
        }
        // 保存数据
        baseMapper.insert(sysDict);
        // 更新祖籍编号
        lambdaUpdate().set(SysDict::getAncestors, parentAncestors + sysDict.getId() + ",").eq(SysDict::getId, sysDict.getId()).update();
    }

    @Override
    public void remove(Integer id){
        SysDict sysDict = lambdaQuery().select(SysDict::getId, SysDict::getAncestors).eq(SysDict::getId, id).one();
        if (sysDict == null){
            throw new ISystemException("该字典不存在");
        }
        lambdaUpdate().likeRight(SysDict::getAncestors, sysDict.getAncestors()).remove();
    }

    @Override
    public List<SysDictTreeResp> tree(String key, Integer level){
        List<SysDictTreeBo> list = baseMapper.selectDictList(key, level);
        return MapstructUtil.convert(buildTree(list), SysDictTreeResp.class);
    }

    @Override
    public List<SysDictBaseTreeResp> baseTree(String key, Integer level){
        List<SysDictTreeBo> list = baseMapper.selectBaseList(key, level);
        return MapstructUtil.convert(buildTree(list), SysDictBaseTreeResp.class);
    }

    /**
     * 将list转化为树形结构
     * @param list
     * @return
     */
    private static List<SysDictTreeBo> buildTree(List<SysDictTreeBo> list){

        // 首先将id和对象映射成一个map
        Map<Integer, SysDictTreeBo> map = list.stream().collect(Collectors.toMap(SysDictTreeBo::getId, o -> o));
        // 建立一个集合用于存放根节点列表
        List<SysDictTreeBo> rootList = new ArrayList<>();

        // 遍历每个节点，找到对应的父节点
        for (SysDictTreeBo dict : list){
            if (dict.getParentId() == BASE_ID){
                rootList.add(dict);
            }else {
                // 找到自己的父节点，将自己存进去
                SysDictTreeBo parent = map.get(dict.getParentId());
                if (parent == null){
                    // 没找到父节点，自己就是根节点
                    rootList.add(dict);
                    continue;
                }
                if (parent.getType() != SysDictTypeEnum.MENU && parent.getType() != SysDictTypeEnum.ARRAY){
                    continue;
                }
                if (parent.getValue() == null || StringUtils.isBlank(String.valueOf(parent.getValue()))){
                    parent.setValue(new ArrayList<>());
                }
                ((ArrayList)(parent.getValue())).add(dict);
            }
        }
        return rootList;
    }

}




