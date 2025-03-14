package com.luma.common.domain;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 基础分页对象
 */
@Data
public class BasePage extends Sorter {

    /**
     * 页号
     */
    @NotNull(message = "页号不能为空")
    private Integer current;

    /**
     * 页大小
     */
    @NotNull(message = "页大小不能为空")
    private Integer size;

    /**
     * 转为mybatis-plus的分页对象
     * @return
     */
    public <T> IPage<T> toMpPage(){
        return toMpPage(null);
    }

    /**
     * 转为mybatis-plus的分页对象
     * @param alias 别名 用于排序字段指定表的别名
     * @return
     */
    public <T> IPage<T> toMpPage(String alias) {
        Page<T> page = Page.of(current, size);
        List<OrderItem> orderItems = getSorts(alias);
        if (orderItems == null || orderItems.isEmpty()) {
            return page;
        }else {
            return page.addOrder(orderItems);
        }
    }

}
