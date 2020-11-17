package com.gyc.demo.controller;

import com.gyc.demo.service.IStudentService;
import com.gyc.demo.service.ITeacherService;
import com.gyc.mvcframework.annonation.GYCAutowired;
import com.gyc.mvcframework.annonation.GYCController;
import com.gyc.mvcframework.annonation.GYCRequestMapping;
import com.gyc.mvcframework.annonation.GYCRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by GYC
 * 2020/6/17 16:46
 */
@GYCController
@GYCRequestMapping("/stu")
public class StudentController {
    @GYCAutowired
    private IStudentService studentService;

    private ITeacherService teacherService;


    @GYCRequestMapping("/query/")
    public void getStuByID(HttpServletRequest req, HttpServletResponse resp,
                           @GYCRequestParam("id") String id,
                           @GYCRequestParam("name") String name) {
        try {
            System.out.println("调用/stu/query/ "+id+" "+name);
            resp.getWriter().write("WELCOME!!!  ID " + id + ", NAME " + name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GYCRequestMapping("/delete")
    public void deleteStuByID(HttpServletRequest req, HttpServletResponse resp,
                              @GYCRequestParam("id") String id,
                              @GYCRequestParam("name") String name,
                              @GYCRequestParam("address") String addr) {
        try {
            System.out.println("调用/stu/query/ " + id + ", " + name + ", " + addr);
            resp.getWriter().write("delete " + id + ", " + name + ", " + addr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
