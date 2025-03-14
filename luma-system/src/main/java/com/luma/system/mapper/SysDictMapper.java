package com.luma.system.mapper;

import com.luma.system.domain.bo.SysDictTreeBo;
import com.luma.system.domain.entity.SysDict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 92755
* @description 针对表【sys_dict】的数据库操作Mapper
* @createDate 2025-01-22 11:24:46
* @Entity com.luma.system.domain.entity.SysDict
*/
public interface SysDictMapper extends BaseMapper<SysDict> {

    /**
     * 获取字典列表
     * @param key 指定key
     * @param level 指定层级
     * @return
     */
    List<SysDictTreeBo> selectDictList(@Param("key") String key, @Param("level") Integer level);

    /**
     * 获取字典列表（最小单元）
     * @param key 指定key
     * @param level 指定层级
     * @return
     */
    List<SysDictTreeBo> selectBaseList(@Param("key") String key, @Param("level") Integer level);

}




