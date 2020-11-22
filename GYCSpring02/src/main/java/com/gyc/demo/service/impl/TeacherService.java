package com.gyc.demo.service.impl;

import com.gyc.demo.service.ITeacherService;
import com.gyc.mvcframework.annonation.GYCService;

/**
 * Created by GYC
 * 2020/11/13 9:58
 */
@GYCService("TeacherService")
public class TeacherService implements ITeacherService {
    @Override
    public void teach() {
        System.out.println("in TeacherService teach");
    }
}
