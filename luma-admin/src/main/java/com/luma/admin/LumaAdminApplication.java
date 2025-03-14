package com.luma.admin;

import com.luma.framework.config.WebProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author 刘靖
 */
@EnableAsync
@EnableCaching
@SpringBootApplication
@MapperScan("com.luma.**.mapper")
@ComponentScan(basePackages = "com.luma")
@EnableConfigurationProperties(WebProperties.class)
public class LumaAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(LumaAdminApplication.class, args);
        // https://www.bootschool.net/ascii of 3d
        System.out.println("启动成功");
    }

}
