package com.gyc.appConifg;

import com.gyc.pojo.GraduateStudent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by GYC
 * 2020/11/18 17:13
 * <p>
 * 使用纯java的方式（JavaConfig）配置Spring
 * 而非xml文件
 */

@Configuration
@ComponentScan("com.gyc.pojo")
public class GYCConfig {
    @Bean
    public GraduateStudent graduateStudent() {
        return new GraduateStudent();
    }

}
