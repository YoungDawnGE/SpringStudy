package com.gyc.demo.service.impl;

import com.gyc.demo.service.IStudentService;
import com.gyc.mvcframework.annonation.GYCService;

/**
 * Created by GYC
 * 2020/6/18 23:05
 */
@GYCService("StudentService")
public class StudentService implements IStudentService {
    @Override
    public void study() {
        System.out.println("in StudentService study");
    }
}
