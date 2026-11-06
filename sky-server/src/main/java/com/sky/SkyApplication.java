package com.sky;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 苍穹外卖服务启动类
 */
@SpringBootApplication
@EnableTransactionManagement // 开启注解式事务
@EnableCaching              // 开启 Spring Cache 缓存注解
@EnableScheduling           // 开启 Spring Task 定时任务
public class SkyApplication {

    public static void main(String[] args) {
        SpringApplication.run(SkyApplication.class, args);
        System.out.println("===== 苍穹外卖服务启动成功 =====");
    }
}
