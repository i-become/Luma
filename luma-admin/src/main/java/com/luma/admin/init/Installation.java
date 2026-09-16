package com.luma.admin.init;

import com.luma.framework.config.AwsS3Config;
import com.luma.framework.utils.AwsS3Util;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.model.Bucket;

import java.util.List;

/**
 * 系统启动时做一些初始化的处理
 * @author i-become
 */
@Log4j2
@Component
public class Installation {

    @Resource
    private AwsS3Config awsS3Config;

    /**
     * 初始化方法
     */
    @EventListener(ApplicationReadyEvent.class)
    public void init(){
        try {
            log.info("系统初始化 - 开始");
            // 初始化对象存储
            initMinio();
        }catch (Exception e){
            log.error("系统初始化 - 失败", e);
//            System.exit(0);
        }
    }

    /**
     * 初始化对象存储
     */
    public void initMinio(){
        String awsBucket = awsS3Config.getBucket();
        // 判断需要的对象存储bucket是否创建，没有则自动创建
        List<Bucket> buckets = AwsS3Util.listBuckets();
        for(Bucket bucket : buckets){
            if (awsBucket.equals(bucket.name())){
                log.info("系统初始化 - 对象存储bucket已存在，无需重复创建");
                return;
            }
        }
        log.info("系统初始化 - 对象存储bucket不存在，创建bucket:{}", awsBucket);
        AwsS3Util.createBucket(awsBucket);
        log.info("系统初始化 - 对象存储bucket成功");
    }

}