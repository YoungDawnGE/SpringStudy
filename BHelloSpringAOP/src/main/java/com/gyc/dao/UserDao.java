package com.gyc.dao;

import com.gyc.pojo.User;

import java.util.List;

/**
 * Created by GYC
 * 2020/11/19 12:51
 */
public interface UserDao {
    /**
     * limit offset,len
     * @param offset 查询起点
     * @param len 查询长度
     * @return
     */
    List<User> selectAllUsers(int offset, int len);

    User selectUserById(int id);

}
