package com.gyc.pojo;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by GYC
 * 2020/11/17 10:21
 */
public class Student {
    private String id;
    private String name;
    private String phone;
    private Address address;
    private String[] books;
    private List<String> friends;
    private Map<String, String> leetcode;
    private Set<String> games;

    public Student() {
        System.out.println("Student默认无参构造器被执行");
    }

    public Student(String id) {
        System.out.println("有参构造器赋值");
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
