package com.luma.common.domain;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库动态排序
 */
@Data
public class Sorter {

    /**
     * 排序字段
     */
    private List<OrderItem> sorts;

    /**
     * 获取排序字段，会自动转下划线
     * @return
     */
    public List<OrderItem> getSorts() {
        return getSorts(null);
    }

    /**
     * 获取排序字段，会自动拼接别名和转下划线
     * @param alias 别名
     * @return
     */
    public List<OrderItem> getSorts(String alias) {
        if (sorts == null || sorts.isEmpty()) {
            return sorts;
        }
        // 别名处理
        alias = alias == null ? "" : (alias + ".");

        // 驼峰转下划线，不能在原来的对象上进行修改，防止多次调用别名累计
        boolean isNew = false;
        List<OrderItem> result = new ArrayList<>();
        for (OrderItem sort : sorts) {
            if (StringUtils.isBlank(sort.getColumn())){
                continue;
            }
            isNew = true;
            if (sort.isAsc()){
                result.add(OrderItem.asc(alias + StrUtil.toUnderlineCase(sort.getColumn())));
            }else {
                result.add(OrderItem.desc(alias + StrUtil.toUnderlineCase(sort.getColumn())));
            }
        }
        // 判断是否存在修改，如果没有修改，就是第一次进行序列化的操作，直接返回原始对象
        return isNew ? result : sorts;
    }

    /**
     * 根据查询条件拼接得到order by语句
     *
     * @param alias 别名
     * @return String
     */
    private String getStatement(String alias) {

        if (sorts == null || sorts.isEmpty()){
            return "";
        }

        // 别名处理
        alias = alias == null ? "" : (alias + ".");

        StringBuilder statement = new StringBuilder();
        for (OrderItem orderItem : sorts) {
            // 驼峰转下划线，并去除空格、换行符
            String column = StrUtil.removeAllLineBreaks(StrUtil.cleanBlank(StrUtil.toUnderlineCase(orderItem.getColumn())));
            statement.append(alias).append(column).append(orderItem.isAsc() ? " ASC" : " DESC");
        }

        return statement.toString();
    }

}
