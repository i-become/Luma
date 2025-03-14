package com.luma.admin;

import com.baomidou.mybatisplus.core.plugins.IgnoreStrategy;
import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LumaAdminApplicationTests {

    @Test
    void contextLoads() {
    }

    public static void main(String[] args) {

        InterceptorIgnoreHelper.handle(IgnoreStrategy.builder().tenantLine(true).build());
        try {
            // 业务处理
        } catch (Exception e) {
            // 清理线程
            InterceptorIgnoreHelper.clearIgnoreStrategy();
        }

    }

}
