package com.gyc.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by GYC
 * 2020/11/19 13:13
 * 这个类用于配置bean
 * 暂时不能用以下
 * ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
 * 来获取IOC容器，因为所有的AOP目前是写在applicationContext.xml中的，
 * 我暂时还没把AOP的注解写在程序中
 * 应该用ClassPathXmlApplicationContext("applicationContext.xml")
 */

@Configuration
@ComponentScan({
        "com.gyc.a01log",
        "com.gyc.controller",
        "com.gyc.service",
        "com.gyc.dao",
        "com.gyc.pojo"})
public class AppConfig {
}
