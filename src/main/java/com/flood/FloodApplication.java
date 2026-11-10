package com.flood;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启动类
 */
@SpringBootApplication
@MapperScan("com.flood.mapper")
@EnableScheduling  // I: 启用定时任务 (阈值预警自动触发)
public class FloodApplication {
    public static void main(String[] args) {
        SpringApplication.run(FloodApplication.class, args);
    }
}
