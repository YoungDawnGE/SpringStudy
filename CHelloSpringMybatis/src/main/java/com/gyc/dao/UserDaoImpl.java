package com.gyc.dao;

import com.gyc.pojo.User;
import org.mybatis.spring.SqlSessionTemplate;

import java.util.List;

/**
 * Created by GYC
 * 2020/11/20 17:36
 *
 * 记得把这个类在XML里面注入
 * 它需要一个sqlSessionTemplate参数
 *
 */

public class UserDaoImpl implements UserDao{
    //我们所有的操作都由SqlSessionTemplate就是SqlSession来完成
    private SqlSessionTemplate sqlSessionTemplate;

    //提供一个set方法是为了让Spring注入bean
    public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
        this.sqlSessionTemplate = sqlSessionTemplate;
    }

    @Override
    public List<User> selectAllUsers(int offset, int len) {
        UserDao mapper = sqlSessionTemplate.getMapper(UserDao.class);
        return mapper.selectAllUsers(offset, len);
    }

    @Override
    public User selectUserById(int id) {
        UserDao mapper = sqlSessionTemplate.getMapper(UserDao.class);
        return mapper.selectUserById(id);
    }
}
