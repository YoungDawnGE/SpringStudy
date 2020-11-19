package com.gyc.pojo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by GYC
 * 2020/11/18 17:12
 */
@Component
public class GraduateStudent {
    @Value("20201118")
    private String id;
    private String name;
    private String phone;

    @Override
    public String toString() {
        return "GraduateStudent{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
