package com.gyc.pojo;

import org.springframework.stereotype.Component;

/**
 * Created by GYC
 * 2020/11/18 17:53
 * 用于测试appConfig的配置类
 * 看看用了@ComponentScan，是不是就可以不用@Bean了
 */
@Component
public class Professor {
    private String id;
    private String name;

    @Override
    public String toString() {
        return "Professor{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
