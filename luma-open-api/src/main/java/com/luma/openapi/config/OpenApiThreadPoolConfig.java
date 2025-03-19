package com.luma.openapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author i-become
 */
@Configuration
public class OpenApiThreadPoolConfig {

    /**
     * 推送线程池
     * @return
     */
    @Bean(name = "pushThreadExecutor")
    public Executor pushThreadExecutor() {
        // 获取CPU核心数
        int cpuCores = Runtime.getRuntime().availableProcessors();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 对于I/O密集型任务，线程数通常设置为 CPU核心数的2倍或更多
        // 核心线程数
        executor.setCorePoolSize(cpuCores * 2);
        // 最大线程数
        executor.setMaxPoolSize(cpuCores * 4);
        // 队列容量更大
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("Client-Push-");
        // 拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

}
