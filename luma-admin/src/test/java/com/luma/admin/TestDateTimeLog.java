package com.luma.admin;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 *
 */
@Slf4j
public class TestDateTimeLog {


    public static void main(String[] args) {
        final DateTime dateTime = DateUtil.offsetDay(new Date(), -7);
        log.info("toString:[{}]", dateTime.toString());   // 2025-02-05 14:37:31
        log.info("dateTime:[{}]", dateTime);              // 2025-02-05T14:37:31.245+0800
    }


}
