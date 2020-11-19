package com.gyc.service.serviceImpl;

import com.gyc.service.UserService;
import org.springframework.stereotype.Service;

/**
 * Created by GYC
 * 2020/11/19 12:47
 */
//@Service
public class UserServiceImpl implements UserService {

    @Override
    public void addUser() {
        System.out.println("addUser");
    }

    @Override
    public void deleteUser() {
        System.out.println("deleteUser");
    }

    @Override
    public void updateUser() {
        System.out.println("updateUser");
    }

    @Override
    public void getUser() {
        System.out.println("getUser");
    }
}
